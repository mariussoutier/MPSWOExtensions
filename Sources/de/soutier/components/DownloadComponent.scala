package de.soutier.components.common

import scala.reflect.BeanProperty

import java.io.File

import org.apache.log4j.Logger

import com.webobjects.foundation.NSData
import com.webobjects.appserver.{WOContext, WOResponse}
import er.extensions.components.ERXStatelessComponent
import er.javamail.ERMimetypesMapper

/**
 * Generic download component.
 * 
 * @binding downloadFile
 * @binding stringContent
 * @binding customFileName
 */
class DownloadComponent(context: WOContext) extends ERXStatelessComponent(context) {
	private lazy val logger = Logger.getLogger(this.getClass.getName)
	
	@BeanProperty var downloadFile: File = null
	@BeanProperty var stringContent: String = null
	@BeanProperty var customFileName: String = null

	override def appendToResponse(aResponse: WOResponse, context: WOContext) {
		def createNSData() = {
			if (stringContent != null)
				new NSData(stringContent, "UTF-8")
			else if (downloadFile != null)
				new NSData(getBytesFromFile(downloadFile))
			else
				new NSData
		}
		def mimeType = if (downloadFile != null) ERMimetypesMapper.mimeContentTypeForPath(downloadFile.getPath) else "text/plain"
		def fileName = if (customFileName != null) customFileName else if (downloadFile != null) downloadFile.getName else "download"

		val downloadData = createNSData
		aResponse.setContent(downloadData)
		aResponse.setHeader(mimeType, "content-type")
		aResponse.setHeader("attachment; filename=" + fileName, "content-disposition")
		aResponse.setHeader((new Integer(downloadData.length)).toString, "content-length")
		aResponse.disableClientCaching
		aResponse.removeHeadersForKey("Cache-Control")
		aResponse.removeHeadersForKey("pragma")
	}

	private def getBytesFromFile(file: File): Array[Byte] = {
		val is = new java.io.FileInputStream(file)
		val length = file.length.toInt

    if (length > Integer.MAX_VALUE) {
      logger.error("File is too large!")
      return new Array[Byte](0)
    }

    val bytes = new Array[Byte](length)
    var offset = 0
    var numRead = 0
    while (offset < bytes.length && (numRead >= 0)) {
      numRead = is.read(bytes, offset, bytes.length - offset)
      offset += numRead
    }

    if (offset < bytes.length) {
      is.close
      throw new java.io.IOException("Could not completely read file "	+ file.getName)
    }

    is.close
    bytes
  }
}