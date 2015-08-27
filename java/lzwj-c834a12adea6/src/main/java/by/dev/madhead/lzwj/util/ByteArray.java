package by.dev.madhead.lzwj.util;

import java.util.Arrays;

public class ByteArray {
	private byte[] internal;

	public ByteArray() {
		internal = new byte[0];
	}

	public ByteArray(ByteArray another) {
		internal = another.internal.clone();
	}

	public ByteArray(byte[] array) {
		internal = array.clone();
	}

	public ByteArray(byte b1, byte... bytes) {
		int bytesSize = (bytes != null) ? bytes.length : 0;

		internal = new byte[bytesSize + 1];
		internal[0] = b1;
		for (int i = 1; i < internal.length; i++) {
			internal[i] = bytes[i - 1];
		}
	}

	public int size() {
		return internal.length;
	}

	public byte get(int index) {
		return internal[index];
	}

	public void set(int index, byte value) {
		internal[index] = value;
	}

	public ByteArray append(ByteArray another) {
		int size = size();
		int anotherSize = another.size();
		int newSize = size + anotherSize;
		byte[] newBuf = new byte[newSize];

		for (int i = 0; i < size; i++) {
			newBuf[i] = get(i);
		}
		for (int i = 0; i < anotherSize; i++) {
			newBuf[i + size] = another.get(i);
		}
		internal = newBuf;
		return this;
	}

	public ByteArray append(byte[] array) {
		return append(new ByteArray(array));
	}

	public ByteArray append(byte b1, byte... bytes) {
		return append(new ByteArray(b1, bytes));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ByteArray other = (ByteArray) obj;
		if (!Arrays.equals(internal, other.internal))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(internal);
		return result;
	}

	@Override
	public String toString() {
		return "ByteArray [internal=" + Arrays.toString(internal) + "]";
	}
}
