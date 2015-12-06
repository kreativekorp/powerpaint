package com.kreative.paint.material.frame;

public class CRCCalculator {
	private final int[] crcTable;
	
	public CRCCalculator() {
		crcTable = new int[256];
		for (int n = 0; n < 256; n++) {
			int c = n;
			for (int k = 0; k < 8; k++) {
				if ((c & 1) != 0) {
					c = 0xEDB88320 ^ (c >>> 1);
				} else {
					c = c >>> 1;
				}
			}
			crcTable[n] = c;
		}
	}
	
	public int updateCRC(int crc, byte[] data) {
		for (int n = 0; n < data.length; n++) {
			crc = crcTable[(crc ^ data[n]) & 0xFF] ^ (crc >>> 8);
		}
		return crc;
	}
	
	public int calculateCRC(byte[] data) {
		return updateCRC(-1, data) ^ -1;
	}
}
