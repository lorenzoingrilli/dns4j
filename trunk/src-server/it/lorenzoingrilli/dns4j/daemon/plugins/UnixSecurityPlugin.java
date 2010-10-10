/*
 * Copyright (C) 2010 - Lorenzo Ingrilli' - info@lorenzoingrilli.it
 * 
 * Licensed under LGPL3 only.
 * See http://www.gnu.org/licenses/lgpl.html
 */
package it.lorenzoingrilli.dns4j.daemon.plugins;

import java.beans.ConstructorProperties;

import it.lorenzoingrilli.dns4j.daemon.Plugin;
import it.lorenzoingrilli.dns4j.daemon.Kernel;

import com.sun.jna.Native;

/**
 * Unix Security Plugin
 * 
 * Enhance security in unix enviroment
 * Warning: depends on JNA (>=3.2) (not bundled with dns4j)
 *  
 * @author Lorenzo Ingrilli
 *
 */
public class UnixSecurityPlugin implements Plugin {
	
	private long uid;
	private long gid;
	private String chroot;
	
	@ConstructorProperties(value={"uid", "gid"})
	public UnixSecurityPlugin(long uid, long gid) {
		this.uid = uid;		
		this.gid = gid;
	}
	
	@Override
	public void init(Kernel kernel) {
		long r = 0;
		// CHROOT
		if(chroot!=null) {
			r = Libc.chroot(chroot);
			if(r!=0) {
				System.err.println("Can't chroot to "+chroot+", error code="+r);
				System.exit(1);
			}
		}
		// SET GID
		r = Libc.setgid(gid);
		if(r!=0) {
			System.err.println("Can't set GID="+gid+", error code="+r);
			System.exit(1);
		}
		// SET UID
		r = Libc.setuid(uid);
		if(r!=0) {
			System.err.println("Can't set UID="+uid+", error code="+r);
			System.exit(1);
		}
	}

	@Override
	public void destroy() {
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}
	
	public long getGid() {
		return gid;
	}

	public void setGid(long gid) {
		this.gid = gid;
	}

	public String getChroot() {
		return chroot;
	}

	public void setChroot(String chroot) {
		this.chroot = chroot;
	}

}

class Libc {
	
	private static final String LIBC = "libc";
	
	static {
		Native.register(LIBC);
	}
		
	public static native long getuid();
	public static native long setuid(long uid);
	public static native long getgid();
	public static native long setgid(long gid);
	public static native long chroot(String path);

}
