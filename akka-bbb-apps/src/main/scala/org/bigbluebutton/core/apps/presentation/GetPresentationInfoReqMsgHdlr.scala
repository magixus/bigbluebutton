package org.bigbluebutton.core.apps.presentation

import org.bigbluebutton.core.OutMessageGateway
import org.bigbluebutton.common2.messages._
import org.bigbluebutton.common2.domain.PresentationVO
import org.bigbluebutton.common2.messages.Presentation.{ GetPresentationInfoReqMsg, GetPresentationInfoRespMsg, GetPresentationInfoRespMsgBody }
import org.bigbluebutton.core.apps.Presentation

trait GetPresentationInfoReqMsgHdlr {
  this: PresentationApp2x =>

  val outGW: OutMessageGateway

  def handleGetPresentationInfoReqMsg(msg: GetPresentationInfoReqMsg): Unit = {
    log.debug("Received GetPresentationInfoReqMsg")

    def broadcastEvent(msg: GetPresentationInfoReqMsg, presentations: Vector[Presentation]): Unit = {
      val routing = Routing.addMsgToClientRouting(MessageTypes.DIRECT, liveMeeting.props.meetingProp.intId, msg.header.userId)
      val envelope = BbbCoreEnvelope(GetPresentationInfoRespMsg.NAME, routing)
      val header = BbbClientMsgHeader(GetPresentationInfoRespMsg.NAME, liveMeeting.props.meetingProp.intId, msg.header.userId)

      val presVOs = presentations.map { p =>
        PresentationVO(p.id, p.name, p.current, p.pages.values.toVector, p.downloadable)
      }

      val body = GetPresentationInfoRespMsgBody(presVOs)
      val event = GetPresentationInfoRespMsg(header, body)
      val msgEvent = BbbCommonEnvCoreMsg(envelope, event)
      outGW.send(msgEvent)
    }

    broadcastEvent(msg, getPresentationInfo())
  }
}
