/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.radixdlt.crypto.hdwallet;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.core.util.Integers;

/**
 * A set of utility methods used for e.g. validating strings representing {@link HDPath}.
 */
public final class HDPaths {

	private HDPaths() {
		throw new IllegalStateException("Can't construct.");
	}

	public static final String BIP39_MNEMONIC_NO_PASSPHRASE = "";
	public static final String BIP32_HARDENED_MARKER_STANDARD = "'";

	public static final String BIP32_PATH_SEPARATOR = "/";
	public static final String BIP32_PREFIX_PRIVATEKEY = "m";

	public static final long BIP32_HARDENED_VALUE_INCREMENT = 0x80000000L;

	static void validateHDPathString(String path) throws HDPathException {
		if (!isValidHDPath(path)) {
			throw new HDPathException("Invalid BIP32 path");
		}
	}

	/**
	 * Checks if the {@code path} string is a valid BIP32 path or not, using the standard BIP32
	 * hardened marker {@link #BIP32_HARDENED_MARKER_STANDARD}.
	 * @param path to validate
	 * @return true iff {@code path} is a valid BIP32 path, else false.
	 */
	static boolean isValidHDPath(String path) {
		return isValidHDPath(path, BIP32_HARDENED_MARKER_STANDARD);
	}

	/**
	 * Checks if the {@code path} string is a valid BIP32 path or not.
	 * @param path to validate
	 * @param hardenedMarker the string used to mark hardened paths
	 * @return true iff {@code path} is a valid BIP32 path, else false.
	 */
	static boolean isValidHDPath(String path, String hardenedMarker) {
		// Check trivial paths
		if (ImmutableList.of("", BIP32_PREFIX_PRIVATEKEY, BIP32_PATH_SEPARATOR).contains(path)) {
			return true;
		}
		if (path.startsWith("M/") || path.startsWith("m/")) {
			path = path.substring(2);
		}

		if (path.isEmpty()) {
			return false;
		}

		if (path.contains("//")) {
			return false;
		}

		for (String component : path.split(BIP32_PATH_SEPARATOR)) {
			if (component.isEmpty()) {
				return false;
			}
			if (component.endsWith(hardenedMarker)) {
				component = component.replace(hardenedMarker, "");
			}
			try {
				if (Integers.parseInt(component) < 0) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}

		}

		return true;
	}
}
