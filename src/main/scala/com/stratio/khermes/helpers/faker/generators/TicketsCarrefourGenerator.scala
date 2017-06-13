/*
 * Copyright (C) 2016 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.khermes.helpers.faker.generators

import com.stratio.khermes.commons.constants.AppConstants
import com.stratio.khermes.commons.exceptions.KhermesException
import com.stratio.khermes.commons.implicits.AppSerializer
import com.stratio.khermes.helpers.faker.FakerGenerator
import com.typesafe.scalalogging.LazyLogging
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}

case class TicketsCarrefourGenerator(locale: String) extends FakerGenerator
  with AppSerializer
  with LazyLogging {

  override def name: String = "tickets"

  val resourcesFiles = Seq("ticketsCarrefour.json")

  lazy val ticketModel = {
    locale match {
      case AppConstants.DefaultLocale | "EN" | "ES" =>
        logger.info("Tickets from all resources .... ")
        val resources = resourcesFiles.map(parse[Seq[TicketCarrefour]](name, _))
        if (parseErrors[Seq[TicketCarrefour]](resources).nonEmpty) {
          logger.warn(s"${parseErrors[Seq[TicketCarrefour]](resources)}")
        }
        resources
      case localeValue =>
        logger.info("Tickets from one resource .... ")
        Seq(parse[Seq[TicketCarrefour]](name, s"$localeValue.json"))
    }
  }.filter(_.isRight).flatMap(_.right.get.map(ticketToTicketModel))

  def generateTicket: TicketCarrefourModel =
    Try {
      randomElementFromAList[TicketCarrefourModel](ticketModel).get
    } match {
      case Success(ticket) =>
        ticket
      case Failure(e) =>
        throw new KhermesException(s"Error loading locate /locales/$name/$locale with exception: ${e.getLocalizedMessage}")
    }

  def ticketToTicketModel(ticket: TicketCarrefour): TicketCarrefourModel = {
    TicketCarrefourModel(
      Option(ticket.id).getOrElse(""),
      ticket.clientId,
      Option(ticket.date).getOrElse(""),
      Option(ticket.mallNumber).getOrElse(""),
      Option(ticket.shaOne).getOrElse(""),
      ticket.clubCardNumber,
      ticket.items.map(item => write(item)),
      Option(write(ticket.header)).getOrElse("{}"),
      ticket.vatItems.map(vatItem => write(vatItem)),
      ticket.lines,
      ticket.concepts.map(concept => write(concept)),
      Option(ticket.numberAuxiliaryData).getOrElse(""),
      Option(ticket.auxiliaryData).getOrElse("")
    )
  }
}

case class TicketCarrefourModel(
                        id: String,
                        clientId: Long,
                        date: String,
                        mallNumber: String,
                        shaOne: String,
                        clubCardNumber: Long,
                        items: Array[String],
                        header: String,
                        vatItems: Array[String],
                        lines: Array[String],
                        concepts: Array[String],
                        numberAuxiliaryData: String,
                        auxiliaryData: String
                      )

case class TicketCarrefour(id: String, clientId: Long, date: String, mallNumber: String, shaOne: String,
                           clubCardNumber: Long, items: Array[ItemTicketCarrefour], header: HeaderCarrefour, vatItems: Array[VatItemsCarrefour],
                           lines: Array[String], concepts: Array[ConceptsCarrefour], numberAuxiliaryData: String,
                           auxiliaryData: String)

case class ItemTicketCarrefour(code: Long, vat: Int, netAmount: Double,
                               numberUnits: Int, subFamily: Long, description: String,
                               numberAuxiliaryData: String, auxiliaryData: String)

case class VatItemsCarrefour(base: Double, vat: Double, share: Int)

case class ConceptsCarrefour(code: Long, `type`: Int, description: String, amount: Double)

case class HeaderCarrefour(netAmount: Double, numberItems: Int, numberLines: Int, numberLinesItems: Int,
                           numberLinesVat: Int, operator: String, cashNumber: String, ticketType: String, ticketNumber: String,
                           typeOperation: String)