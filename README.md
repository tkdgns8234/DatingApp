# DatingApp
데이팅 앱 만들기
# 사용 기술
- Kotlin
- MVVM
- Clean Architecture
- Koin
- Coroutine
- FirebaseAuth
- Realtime DB
- Storage
- ViewBinding
- Glide
- Navigation


# 💡 Topic

- 소개팅 상대 매칭 앱

# 📝 Summary

- 상대방의 프로필을 확인한 후에 좌 우로 스와이프하여 like, dislike를 할 수 있으며 **서로 like 한 경우 매칭되어 채팅을 할 수 있다**

# ⭐️ Key Function

- 사용자 로그인 및 회원가입 기능 구현
- 갤러리에서 이미지를 불러와 프로필 설정
- 좌, 우로 스와이프하여 like, dislike 할 수 있음
- 서로 like 한 경우 매칭되어 채팅을 할 수 있음

# ⚙️ Architecture

- `MVVM`, `Clean Architecture`

# 🤔 Learned

- MVVM 및 Clean Architecture 학습하고 적용해보기
    - **[클린 아키텍처에대해 배운 점](https://tkdgns8234.tistory.com/196)**
    - **[아키텍처 패턴에 대해](https://tkdgns8234.tistory.com/190)**
- 의존성 주입 라이브러리 Koin 적용
- Coroutine 적용
- Firebase Auth 사용하기
    - email login
    - facebook login
- Firebase Realtime Database
    - DB 구조 설계해보기
- Recyclerview 사용하기 (with Diffutil)
    - listadapter 를 사용하면 diffutill 클래스를 통해 n^2 시간복잡도를 N + D^2 까지 낮출 수 있음
    - **submitList로 데이터를 갱신할 때 유의해야한다. (아래 글 참조)**
- 오픈소스 라이브러리 yuyakaido/CardStackView 사용해 보기


### 개선할 점

보통 VeiwModel의 비대 방지, 비즈니스로직 제거를 위해 Domain 레이어인 UserCase를 도입하는데 현재 작성된 코드는 비즈니스로직이 여전히 ViewModel 에 몰려있어 수정 필요

### **RecyclerView 구현 중 어려웠던 점**

diffutil 클래스를 사용해 데이터를 비교하는 ListAdapter 클래스를 상속하여 RecyclerView를 구현하였는데, submitList() 를 호출하여 데이터를 갱신하여도 갱신되지 않는 문제 발생

관련해서 google sdk 구현을 확인해보니 submitList()로 List 파라미터를 넘길 때 동일한 List를 넘기는 경우 return 시켜버리는 로직을 발견했다

즉, 아래 소스코드에 의해 List 데이터의 참조 주소가 동일하다면 내부 데이터가 변경되어도 UI 변경이 이루어지지 않는다.

```
public void submitList(@Nullable final List<T> newList,
        @Nullable final Runnable commitCallback) {
// incrementing generation means any currently-running diffs are discarded when they finishfinal int runGeneration = ++mMaxScheduledGeneration;

    if (newList == mList) {
// nothing to do (Note - still had to inc generation, since may have ongoing work)if (commitCallback != null) {
            commitCallback.run();
        }
        return;
    }
```
