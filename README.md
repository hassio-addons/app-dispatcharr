# Home Assistant Community App: Dispatcharr

[![GitHub Release][releases-shield]][releases]
![Project Stage][project-stage-shield]
[![License][license-shield]](LICENSE.md)

[![Github Actions][github-actions-shield]][github-actions]
![Project Maintenance][maintenance-shield]
[![GitHub Activity][commits-shield]][commits]

[![Sponsor Frenck via GitHub Sponsors][github-sponsors-shield]][github-sponsors]

[![Support Frenck on Patreon][patreon-shield]][patreon]

Manage IPTV streams, EPG data and DVR recordings.

## About

[Dispatcharr][dispatcharr] takes the IPTV subscriptions you already have and
turns them into one tidy channel list. Point it at your providers and it pulls
in their playlists, works out which of the thousands of streams you actually
want, and puts them in the order you choose. Where the same channel comes from
several providers it keeps all of them and moves on to the next when one stops
working, so a dead stream is something it handles rather than something you
notice.

The guide is the other half. It matches your channels against XMLTV sources or
Schedules Direct, so what comes out has proper programme listings attached, and
a DVR that can record from them on a schedule.

What it hands back is a channel list every media player already understands. It
serves a playlist and a guide, and it pretends to be an HDHomeRun tuner, which
is how Plex, Emby and Jellyfin find it without being told anything beyond an
address.

[:books: Read the full app documentation][docs]

## Support

Got questions?

You have several options to get them answered:

- The [Home Assistant Community Apps Discord chat server][discord] for app
  support and feature requests.
- The [Home Assistant Discord chat server][discord-ha] for general Home
  Assistant discussions and questions.
- The Home Assistant [Community Forum][forum].
- Join the [Reddit subreddit][reddit] in [/r/homeassistant][reddit]

You could also [open an issue here][issue] GitHub.

## Contributing

This is an active open-source project. We are always open to people who want to
use the code or contribute to it.

We have set up a separate document containing our
[contribution guidelines](.github/CONTRIBUTING.md).

Thank you for being involved! :heart_eyes:

## Authors & contributors

The original setup of this repository is by [Franck Nijhof][frenck].

For a full list of all authors and contributors,
check [the contributor's page][contributors].

## We have got some Home Assistant apps for you

Want some more functionality to your Home Assistant instance?

We have created multiple apps for Home Assistant. For a full list, check out
our [GitHub Repository][repository].

## License

MIT License

Copyright (c) 2026 Franck Nijhof

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

[commits-shield]: https://img.shields.io/github/commit-activity/y/hassio-addons/app-dispatcharr.svg
[commits]: https://github.com/hassio-addons/app-dispatcharr/commits/main
[contributors]: https://github.com/hassio-addons/app-dispatcharr/graphs/contributors
[discord-ha]: https://discord.gg/c5DvZ4e
[discord]: https://discord.me/hassioaddons
[docs]: https://github.com/hassio-addons/app-dispatcharr/blob/main/dispatcharr/DOCS.md
[forum]: https://community.home-assistant.io/t/?u=frenck
[frenck]: https://github.com/frenck
[github-actions-shield]: https://github.com/hassio-addons/app-dispatcharr/workflows/CI/badge.svg
[github-actions]: https://github.com/hassio-addons/app-dispatcharr/actions
[github-sponsors-shield]: https://frenck.dev/wp-content/uploads/2019/12/github_sponsor.png
[github-sponsors]: https://github.com/sponsors/frenck
[issue]: https://github.com/hassio-addons/app-dispatcharr/issues
[license-shield]: https://img.shields.io/github/license/hassio-addons/app-dispatcharr.svg
[maintenance-shield]: https://img.shields.io/maintenance/yes/2026.svg
[dispatcharr]: https://github.com/Dispatcharr/Dispatcharr
[patreon-shield]: https://frenck.dev/wp-content/uploads/2019/12/patreon.png
[patreon]: https://www.patreon.com/frenck
[project-stage-shield]: https://img.shields.io/badge/project%20stage-experimental-yellow.svg
[reddit]: https://reddit.com/r/homeassistant
[releases-shield]: https://img.shields.io/github/release/hassio-addons/app-dispatcharr.svg
[releases]: https://github.com/hassio-addons/app-dispatcharr/releases
[repository]: https://github.com/hassio-addons/repository
