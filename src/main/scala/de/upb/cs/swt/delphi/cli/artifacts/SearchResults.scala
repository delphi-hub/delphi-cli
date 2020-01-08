package de.upb.cs.swt.delphi.cli.artifacts

import org.joda.time.DateTime
import de.upb.cs.swt.delphi.cli.artifacts.SearchResultJson._
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

case class SearchResults(totalHits : Long, hits : Array[SearchResult], queried : DateTime = DateTime.now())



object SearchResultsJson extends DefaultJsonProtocol {
  implicit object DateJsonFormat extends RootJsonFormat[DateTime] {

    private val parserISO: DateTimeFormatter = ISODateTimeFormat.dateTime()

    override def write(obj: DateTime) = JsString(parserISO.print(obj))

    override def read(json: JsValue): DateTime = json match {
      case JsString(s) => parserISO.parseDateTime(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }

  implicit val SearchResultsFormat = jsonFormat3(SearchResults)
}
