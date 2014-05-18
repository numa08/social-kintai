package models

trait NavigationBar

case class AuthorizedNavigationBar(name: String, avatar: String) extends NavigationBar
case class UnAuthorizedNavigationBar(url: String) extends NavigationBar