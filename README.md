# Phonograph Plus

<p align="center">
    <img src= "fastlane/metadata/android/en-US/images/icon.png" alt="ICON" height="120"/>
</p>

[English](./README.md) |
[简体中文](./README_ZH.md)
<br/>

> [!CAUTION]
> ~~Phonograph Plus is discontinued!~~
> 
> 🤪 Yes, this is an April Fool's joke! But it is half true. 🥲 The repo would be still in _archived_ for more days. ~Happy April Fool Month/Break!~

<details>

<summary>Original Notice on April 1st</summary>

<b><del>Notice to all users</del></b>

~Google’s upcoming Android policy, _Android developer verification_, will require all developers worldwide to register and be verified by Google before their apps can be installed on certified Android devices — even outside Google Play. Since 2027, _sideloading_ would be extremely difficult for most users. You can get more details from https://keepandroidopen.org/ .~
~**Android is no longer a fully open platform,** Google is killing Android!~
~Phonograph Plus was built on the idea of open distribution. Phonograph Plus refuses such mandatory developer registration, identity verification, and centralized approval.~

~Because of this, **the project cannot continue and will be permanently archived.** Discontinuing Phonograph Plus is both a practical necessity and our response to this policy.~
~Maintenance challenges, an aging codebase, accumulated bugs, and the growing mental burden of sustaining the project also contributed — but they are secondary to the platform changes that fundamentally conflict with our values.~

~Thank you for your support over the past four years.~

~**Farewell.**~

~*April 1*, 2026~

</details>

> [!IMPORTANT]
>
> Starting in 2027 worldwide (or September 2026 for some countries), Android applications from developers without verification centrally through Google Play or the Android Developer Console, _may no longer be installed easily_ on certified Android devices. In simple terms, APKs signed by unregistered signing keys, where developers refuse to upload their personal identification (governmental ID etc.) and their app information (package names and signing key fingerprints etc.) to Google, _may not install easily_ on devices with Google Mobile Services (GMS) where Play Protect is enforced. Apps distributed outside Google Play, just like Phonograph Plus, are severely affected. Although an “advanced flow” may exist, **the "sideloading" would be much more difficult for most users**. And Google is determined without any sign of regret for now. Details and updates can be found [here](https://keepandroidopen.org/).
>
> As a response to this policy, after **April 2027** (or more months), APK artifacts **will no longer be provided in releases but only source code**. Please compile and build Phonograph Plus _yourself_.
>
> If Android becomes further closed and restricted, **Phonograph Plus may be really and eventually discontinued in 2028**, depending on Google's policies next.


[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://github.com/chr56/Phonograph_Plus/blob/release/LICENSE.txt)
[![DownloadsStatistics](https://img.shields.io/github/downloads/chr56/Phonograph_Plus/total)](https://github.com/chr56/Phonograph_Plus/releases)
[![Crowdin](https://badges.crowdin.net/phonograph-plus/localized.svg)](https://crowdin.com/project/phonograph-plus)

[![Stable Release Building Status](https://img.shields.io/github/actions/workflow/status/chr56/Phonograph_Plus/stable_release.yml?label=Stable%20Channel%20building)](https://github.com/chr56/Phonograph_Plus/actions/workflows/stable_release.yml)
[![Preview Release Building Status](https://img.shields.io/github/actions/workflow/status/chr56/Phonograph_Plus/preview_release.yml?label=Preview%20Channel%20building)](https://github.com/chr56/Phonograph_Plus/actions/workflows/preview_release.yml)
[![Dev CI Building Status](https://img.shields.io/github/actions/workflow/status/chr56/Phonograph_Plus/dev.yml?label=Dev%20building)](https://github.com/chr56/Phonograph_Plus/actions/workflows/dev.yml)


**A revived classic material designed music player for Android**

Phonograph is a light-weighted material designed local music player for Android. And Phonograph Plus currently is an _independent_ fork of [Phonograph](https://github.com/kabouzeid/Phonograph) since it is no longer maintained (which has been no longer active after 2020 and has been archived in 2023), and has been in maintenance and development since 2021.

## **Downloads**

[<img src="https://img.shields.io/github/v/release/chr56/phonograph_plus?label=Github%20Releases" alt="Github%20Release">](https://github.com/chr56/Phonograph_Plus/releases/latest)
[<img src="https://img.shields.io/github/v/release/chr56/phonograph_plus?label=Github%20Releases%20(Latest)&include_prereleases" alt="Github%20Release%20(Latest)">](https://github.com/chr56/Phonograph_Plus/releases/)
[<img src="https://img.shields.io/f-droid/v/player.phonograph.plus?label=F-droid" alt="F-droid">](https://f-droid.org/packages/player.phonograph.plus/)

Phonograph Plus is available on GitHub Releases (Stable Channel and Preview Channel[^1]) and F-Droid[^2] (Stable Channel).

[^1]: Package name of Preview version has suffix of `preview`. 

[^2]: Reproducible Builds are enabled on F-droid, Apks from F-droid shall be exactly equivalent to ones from GitHub Releases, byte by byte.


## **Features**

Phonograph Plus inherits all features from [Phonograph](https://github.com/kabouzeid/Phonograph). Here is a notable feature list:

If you're familiar with original Phonograph, please see [Additional Features or Enhancements Compared to Original Phonograph](./docs/Feature_Comparison.md).

- Light weight
- Offline, AD-free and privacy
- Classic Material Design 2
- Night mode (Light, Dark, Black) supporting automatically switching
- Customizable theme color (primary, accent) with Monet support
- Foundational audio playing function (including speed control, sleep timer and equalizer externally provided by system)
- Flexible queue management (shuffle, repeat, freely rearrange by dragging, queue snapshots)
- Lyrics support (both synchronized .lrc lyrics and text lyrics, embed or external)
- Flexible playback notifications that are highly customizable
- Flexible music organization, organised by songs, artist, albums, genres and folders (flatten or treed), ordered by various orders, with
  multiple selection support, viewed in grid or list with various layout style
- Flexible main player user interface that are highly customizable
- Android Auto support
- Highly customizable behaviours for clicking
- Path filter (excluded mode or included mode)
- Search support
- Favorite songs support
- File Playlist support (viewing and basic editing; also pin-able)
- Internal Database Playlist support
- Play history and frequency record, and last added songs by flexible time intervals
- Enhanced audio metadata viewer and tag editor
- App shortcut and widgets
- Multiple source of artwork
- Backup support of settings and data
- In-app language switch
- ...

It is suggested to browser the [Changelog](https://phonographplus.github.io/changelogs/changeslogs/changelog.html) to
view all recent changes, improvements and new features.

## **Screenshot**
See [Gallery](docs/Gallery.md)

## **Translation**

Translate Phonograph Plus into your language -> [Crowdin](https://crowdin.com/project/phonograph-plus)

We have removed Bulgarian, Croatian, Swedish and Norwegian Nynorsk translations due to missing too much.

## **Trouble Shooting & FAQ**
See [Trouble Shooting & FAQ](docs/FAQ.md)

## **Build Instructions & Developer Guide**

See [Build_Instructions.md](docs/Build_Instructions.md)

See [Developer Guide](docs/Developer_Guide.md)

## **Development Plan** & **TO-DO list**

See [Road Map](docs/Road_Map.md)

## **Repository Mirrors**

[![GitHub](https://img.shields.io/badge/Git-Github-Blue)](https://github.com/chr56/Phonograph_Plus/)
[![Codeberg](https://img.shields.io/badge/Git-Codeberg-Blue)](https://codeberg.org/PhonographPlus/Phonograph_Plus)
[![BitBucket](https://img.shields.io/badge/Git-BitBucket-Blue)](https://bitbucket.org/phonograph-plus/phonograph_plus/)

**Only** these three sites above are considered as official Git Repositories (mirroring each other), all the other are non-official.