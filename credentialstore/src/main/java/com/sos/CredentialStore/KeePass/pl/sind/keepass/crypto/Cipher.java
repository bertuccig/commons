/*
 * Copyright 2009 Lukasz Wozniak Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.sos.CredentialStore.KeePass.pl.sind.keepass.crypto;

public interface Cipher {

    public static final String AES = "AES";
    public static final String TWOFISH = "Twofish";

    public String getId();

    public byte[] encrypt(byte[] key, byte[] data, byte[] iv, int rounds, boolean padding) throws CipherException;

    public byte[] decrypt(byte[] key, byte[] data, byte[] iv) throws CipherException;
}
