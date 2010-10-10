/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.protocol.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Serializer;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;

public class SerializerImplTest {

	private static final String CHARSET = "ASCII";
	private static final String MSGFILE = "/it/lorenzoingrilli/dns4j/protocol/impl/messages.yml";
	
	private Serializer serializer = new SerializerImpl();
	private List<TestCase> testCases;
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws IOException {
		InputStream is = getClass().getResourceAsStream(MSGFILE);
		InputStreamReader isr = new InputStreamReader(is, Charset.forName(CHARSET));
		YamlReader reader = new YamlReader(isr);
		YamlConfig config = reader.getConfig();
		config.setClassTag("test", TestCase.class);
		config.setClassTag("message", MessageImpl.class);
		config.setClassTag("header", HeaderImpl.class);
		config.setClassTag("question", QuestionImpl.class);
		config.setPropertyDefaultType(TestCase.class, "msg", MessageImpl.class);
		config.setPropertyDefaultType(MessageImpl.class, "header", HeaderImpl.class);
		config.setPropertyElementType(MessageImpl.class, "questions", QuestionImpl.class);
		
		testCases = (List<TestCase>) reader.read();

		is.close();
		isr.close();		
	}
	
	/** M=d(s(M))*/
	@Test
	public void testA() {
    	for(TestCase tc: testCases) {
    		Message m = tc.getMsg();
        	byte[] buffer = new byte[512];
        	serializer.serialize(m, buffer);
        	Message m1 = serializer.deserialize(buffer, 0, buffer.length);        	
        	Assert.assertTrue(m.equals(m1));
    	}    	
	}
	
	/** Mb=s(d(Mb))*/
	@Test
	public void testB() {
    	for(TestCase tc: testCases) {
    		byte[] buffer = Base64.decodeBase64(tc.getB64());
    		Message m = serializer.deserialize(buffer, 0, buffer.length);
    		byte[] buffer2 = new byte[buffer.length];
        	serializer.serialize(m, buffer2);
        	Assert.assertArrayEquals(buffer, buffer2);		
    	} 
	}
	
	/** Mb=s(m)*/
	@Test
	public void testC() {
    	for(TestCase tc: testCases) {
    		byte[] mb = Base64.decodeBase64(tc.getB64());
    		byte[] buffer2 = new byte[mb.length];
    		serializer.serialize(tc.getMsg(), buffer2);
        	Assert.assertArrayEquals(mb, buffer2);		
    	} 
	}
	
	/** m=d(Mb)*/
	@Test
	public void testD() {
    	for(TestCase tc: testCases) {
    		byte[] mb = Base64.decodeBase64(tc.getB64());
    		Message m = serializer.deserialize(mb, 0, mb.length);
    		Assert.assertTrue(m.equals(tc.getMsg()));		
    	} 
	}
}
