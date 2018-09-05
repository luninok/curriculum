package org.edec.utility.sign.service;

/**
 * @author Max Dimukhametov
 */
public interface SignService {
    boolean createFileAndUpdateUI (byte[] bytesFile, String serialNumber, String thumbPrint);
}
