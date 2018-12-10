# Yona

## Application definition statement

The Yona app enables the user define challenges on mobile device usage and then work on meeting these challenges together with friends.

## References

* [Architecture](Architecture.md)
* [Jira](https://jira.yona.nu/secure/RapidBoard.jspa?rapidView=9&view=detail)
* [Jenkins-Development](https://jenkins-mobile.eu.mobproto.com/job/Yona/)
* [Jenkins-Acceptance](https://jenkins-mobile.eu.mobproto.com/job/Yona-ACC/)
* [Hockey-Development](https://rink.hockeyapp.net/manage/apps/308021)
* [Hockey-Acceptance](https://rink.hockeyapp.net/manage/apps/366916)
* [Server source code](https://github.com/yonadev/yona-server)

## Prerequisites

- Android versions supported `4.4+`
- Build with SDK `27.0.2`
- Works on Android devices

## Environment setup

Checkout and run

## Schemes, targets and build flags

- **zdev**: Development builds. Will work with the development server, this is disable with hockey app crash analytics.
- **zdevlopment**: Development builds. Will work with the development server, this is enable with hockey app crash analytics.
- **zacceptance**: Acceptance/UAT builds. Will work with the acceptance server.

## UAT build

1. Jenkins build
2. Auto clean and build with incremental build version.
3. Upload to HockeyApp
4. Add updates to the HockeyApp release notes
