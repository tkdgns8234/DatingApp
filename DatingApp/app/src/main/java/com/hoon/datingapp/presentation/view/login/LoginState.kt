package com.hoon.datingapp.presentation.view.login

/*
 sealed 클래스는 자기 자신이 추상 클래스이고
  자신을 상속받는 여러 서브 클래스들을 가질 수 있다.
  이를 사용하면 enum 클래스와 달리 상속을 지원하기 때문에,
  상속을 활용한 풍부한 동작을 구현할 수 있다.
 */

sealed class LoginState {

    object Uninitialized : LoginState()

    object Success : LoginState()

    object Error: LoginState()

}