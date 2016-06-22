System Administrator Guide
DNS4J Server

Installation

On Linux:

Install Java 6 Runtime Enviroment on you server
download latest DNS4J release from http://dns4j.sourceforge.net/downloads
put downloaded jar in $JRE_HOME/lib/ext
create configurations files:
mkdir /etc/dns4j
touch /etc/dns4j/nsd.yml
if you choose to use MySQL as zone storage do also:
install mysql jdbc driver in $JRE_HOME/lib/ext
create database (CREATE DATABASE dns4j)
create a database user (GRANT ALL ON dns4j.* TO 'dns4j'@'localhost' IDENTIFIED BY 'password')
initialize database (run java it.lorenzoingrilli.dns4j.nsadmin --install-db -j jdbc:mysql://localhost:3306/dns4j --username=dns4j --password=password)
done
Configuration

The default configuration file is located in /etc/dns4j/nsd.yml

You can configure various component using YAML language:

generic components
plugins
resolvers
A simple configuration file (tcp/udp sever, yaml zones datastore):

---
&yaml !yamlresolver { file: /etc/dns4j/zones.yml }
---
!tcp { port: 5053, resolver: *yaml}
---
!udp { port: 5053, resolver: *yaml}
---
And example yaml zones file (/etc/dns4j/zones.yml):

!zone
name: example.net
rrs:
- !soa { name: example.net, mname: ns.example.net, rname: info.example.net, ttl: 300}
- !txt { name: www.example.net, data: test, ttl: 100 }
- !a { name: www.example.net, address: 10.11.12.13 }
- !mx { name: example.net, exchange: mail.example.net, preference: 10 }
 

Command line utilities

Launch name server daemon:

java it.lorenzoingrilli.dns4j.nsd
Supported Resource Records (RR)

Currently dns4j support following kind of resource records: SOA, NS, A, AAAA, MX, CNAME, TXT, HINFO, PTR, SRV
Unknown RR are also supported.

Resolvers

Following resolvers are supported:

Yaml Resolver. Shortcut !yamlresolver , you should indicate a 'file' properties, this file is the zones file in yaml format.
Database Resolver. Shorcut !dbresolver, you should indicate a 'datasource'.
Scripted Resolver. Shortcut !scriptresolver, you should indicate a 'file' properties. This file is a script which return a DNS Message. All language compatible with "JSR 223: Scripting for the Java™ Platform API" are supported (tested aganist Groovy 1.7 and JRuby 1.9). You can have a statefull script (across multiple executions) by using the 'context' variable (map).

Developing using DNS4J
Query DNS server. Example:

import java.net.InetAddress;
import it.lorenzoingrilli.dns4j.protocol.Clazz;
import it.lorenzoingrilli.dns4j.protocol.Message;
import it.lorenzoingrilli.dns4j.protocol.Type;
import it.lorenzoingrilli.dns4j.protocol.impl.MessageBuilder;
import it.lorenzoingrilli.dns4j.resolver.impl.DNSClient;

public class Test {
    public static void main(String[] args) throws Exception {
        DNSClient client = new DNSClient();
        client.addServer(InetAddress.getByName("8.8.8.8"), 53);
        MessageBuilder mb = new MessageBuilder();  
        Message req =
            mb          
            .question()
            .recursionDesidered(true)
            .addQuestion("www.gentoo.org", Type.A)
            .message();
       
        Message resp = client.query(req);
        System.out.println("REQUEST  "+req);
        System.out.println("RESPONSE "+resp);           
    }    
}


This is the output:

REQUEST  Message(header=Header(id=10531, qr=false, opcode=0, aa=false, tc=false, rd=true, ra=false, z=0, rcode=0, qd=1, an=0, ns=0, ar=0), question=[Question(name=www.gentoo.org, type=1, class=1)], answer=[], authority[], additional=[])
RESPONSE Message(header=Header(id=10531, qr=true, opcode=0, aa=false, tc=false, rd=true, ra=true, z=0, rcode=0, qd=1, an=2, ns=0, ar=0), question=[Question(name=www.gentoo.org, type=1, class=1)], answer=[CNAME(name=www.gentoo.org, ttl=507, cname=www-bytemark.gentoo.org), A(name=www-bytemark.gentoo.org, ttl=507, address=89.16.167.134)], authority[], additional=[])

License
DNS4J is released under LGPLv3 license.

TODO
switch to gradle
Add XML Zones support	 
Add BIND9 Zones compatibility	 
Implement Caching System	 
Add compression support in message serialization	 
Implement DNS-JNDI Provider	 	 
Implement various Resource Records (null, mb, md, mf, mg, minfo, mr, wks, spf, dname, loc, ds, kx, key, naptr, rp, sig, tkey, tsig)
Implement DNSSec specification
Add DNS-Update support	 
Add DNS-Notify support	 
enable resolvers to auto-detect misconfiguration and possible optimization and report it to sysadmin	 	 	 
Implement recursive resolver	 
Create ant target to build packages for various unix distributions
write man-pages
