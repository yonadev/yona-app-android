# External utilities in use

- [OpenVPN](https://github.com/schwabe/ics-openvpn): Open VPN library

# Components inside the project

## APP

The app loads Yona application, it is responsible for managing application with support of all other models.

# Test strategy

## Manual Testing
Manual Testing is done for every task and every screens of application.

## Automation Testing
Automation is in development and script runs as post script on Jenkins for do automation testing.

# Decisions

- OpenVPN is used as per project requirement to connect VPN from application and for same, extracted some code from OpenVPN library and added that in project and do needful changes to make it useful as per project requirement.