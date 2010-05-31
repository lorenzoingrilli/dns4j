package it.lorenzoingrilli.dns4j.protocol.impl;

import it.lorenzoingrilli.dns4j.protocol.Message;

public class TestCase {
	
	private int id;
	private String b64;
	private Message m;
		
	public TestCase() {
	}

	@Override
	public String toString() {
		return "TestCase(id="+id+", b64="+b64+", message="+m+")";
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getB64() {
		return b64;
	}
	public void setB64(String b64) {
		this.b64 = b64;
	}
	public Message getMsg() {
		return m;
	}
	public void setMsg(Message m) {
		this.m = m;
	}
}