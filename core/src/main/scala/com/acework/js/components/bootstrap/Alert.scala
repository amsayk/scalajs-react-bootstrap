package com.acework.js.components.bootstrap

import com.acework.js.utils.{Mappable, Mergeable}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js
import scala.scalajs.js.{UndefOr, undefined}

/**
 * Created by weiyin on 10/03/15.
 */
object Alert extends BootstrapComponent {
  override type P = Props
  override type S = Unit
  override type B = Backend
  override type N = TopNode

  override def defaultProps = Props()

  case class Props(dismissAfter: UndefOr[Int] = undefined,
                   onDismiss: UndefOr[() => Unit] = undefined,
                   bsClass: UndefOr[Classes.Value] = Classes.alert,
                   bsStyle: UndefOr[Styles.Value] = Styles.info,
                   bsSize: UndefOr[Sizes.Value] = undefined,
                   addClasses: String = "") extends BsProps with MergeableProps[Props] {

    def merge(t: Map[String, Any]): Props = implicitly[Mergeable[Props]].merge(this, t)

    def asMap: Map[String, Any] = implicitly[Mappable[Props]].toMap(this)
  }

  class Backend($: BackendScope[Props, Unit]) {
    var dismissTimer: js.UndefOr[js.timers.SetTimeoutHandle] =
      js.undefined

    def clearTimer() = {
      if (dismissTimer.isDefined) {
        js.timers.clearTimeout(dismissTimer.get)
        dismissTimer = undefined
      }
    }

    def start(dismissAfter: Int, onDismiss: () => Unit) =
      dismissTimer = js.timers.setTimeout(dismissAfter)(onDismiss())
  }

  override val component = ReactComponentB[Props]("Alert")
    .stateless
    .backend(new Backend(_))
    .render { (P, C, S, B) =>
    var classes = P.bsClassSet
    val isDismissable = P.onDismiss.isDefined
    classes += ("alert-dismissable" -> isDismissable)

    def renderDismissButton() = {
      <.button(^.tpe := "button", ^.className := "close",
        ^.onClick --> P.onDismiss.get(), ^.aria.hidden := true, ^.dangerouslySetInnerHtml("&times;"))
    }

    // TODO spread props
    <.div(^.classSet1M(P.addClasses, classes))(
      if (isDismissable) renderDismissButton() else EmptyTag,
      C
    )
  }
    .componentDidMount($ =>
    if ($.props.dismissAfter.isDefined && $.props.onDismiss.isDefined)
      $.backend.start($.props.dismissAfter.get, $.props.onDismiss.get)
    )
    .componentWillUnmount($ => $.backend.clearTimer())
    .build

}
