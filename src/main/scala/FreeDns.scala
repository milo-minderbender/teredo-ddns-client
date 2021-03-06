/**
 * (c) 2012 Mark Lister
 * 
 * This software is licenced under the Apache Licence 2.0
 * 
 * Quick and dirty FreeDns.afraid.org client -- written because inadyn-mt wasted several hours of my life
 * This aims to support ip6 / teredo / miredo
 */
package org.catch22.freedns

import java.net.NetworkInterface
import java.net.URL
import java.lang.IllegalArgumentException
import scala.io.Source
import scala.collection.JavaConversions._

object FreeDns extends optional.Application{
  val defaultUrl="http://freedns.afraid.org/dynamic/update.php?%1$s&address=%2$s"

  def main(hashCode:String,url:Option[String],address:Option[String], verbose:Boolean=false): Unit = {
    
    //optionally sniff the teredo address
    val raw=NetworkInterface.getNetworkInterfaces.filter(_.getName.startsWith("teredo"))
                  .flatMap(_.getInterfaceAddresses).map(_.getAddress)
                  .filter(_.toString.startsWith("/2001")).mkString
                  
    val e = raw.lastIndexOf("%")
    val teredoAddr=raw.substring(1, e)
    
    if (verbose) println ("Sniffed teredo addr:"+teredoAddr)
    
    val u= new URL(url getOrElse(defaultUrl) format (hashCode, address.getOrElse(teredoAddr)))
    if(verbose) println ("url:"+u)
    
    val con=u.openConnection
    con.connect
    val result = Source.fromInputStream(con.getInputStream).getLines.mkString
    println(result)
  }
  
}