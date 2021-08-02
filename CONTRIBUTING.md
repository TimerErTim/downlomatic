# Contributing to Downlomatic

[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)

Thank you for considering contributing to this project! Every effort is appreciated and helps tremendously. You are very
welcome to join the team that helped to make Downlomatic a finished and polished project.

These guidelines will help to secure an easy and effective contribution process for all parties involved. This includes
that you respect the time developers maintaining this project put into it. In return, you will be respected by and
during addressing feature ideas, helping you finalize Pull Requests (PRs), and looking into issues you submit.

## Table of Contents

1. [Introduction](#introduction)
2. [Your First Contribution](#your-first-contribution)
3. [Getting Started](#getting-started)
    1. [Issues](#issues)
    2. [Pull Requests](#pull-requests)
        1. [Setup](#setup)
    3. [Adding Hosts](#adding-hosts)
4. [Conventions](#conventions)
    1. [Code Style](#code-style)
    2. [Documentation](#documentation)
    3. [Git and Commit Message](#git-and-commit-messages)
    4. [Versioning Scheme](#versioning-scheme)
5. [Testing](#testing)
6. [Code of Conduct](#code-of-conduct)
7. [License](#license)
8. [Your Recognition](#your-recognition)
9. [Help](#help)

## Introduction

There are many ways to contribute. From submitting bug reports or feature requests to improving the documentation or
extending the source code, everything that helps to move this project forward is a welcome contribution. Since this
project is not well known as of now, advertising and recommending this repository, and the software it provides, is
incredibly helpful and can be done by anyone.

The [issue tracker](https://github.com/TimerErTim/downlomatic/issues) is not suitable for support questions. Checkout
the [Help](#help) section of this document for more information and alternatives.

## Your First Contribution

If you are unsure where to begin or have never contributed to a project before, issues labeled
with [good first issue](https://github.com/TimerErTim/downlomatic/labels/good%20first%20issue)
or [help wanted](https://github.com/TimerErTim/downlomatic/labels/help%20wanted) offer a great opportunity to take part
in the development. Help wanted issues are generally more involved, and a bit more difficult. Nonetheless, they are a
great place to start with.

**Working on your first Pull Request?** You can learn how from this *free*
series [How to Contribute to an Open Source Project on GitHub](https://kcd.im/pull-request)

Now you are ready to directly make changes and improvements to this repository! Feel free to ask for help - every expert
was once a beginner :wink:

## Getting Started

You know how to submit issues and Pull Requests? Have a look at the following guidelines and info regarding both:

- If you find a security vulnerability, do **NOT** open an issue. Email <timerertim@gmail.com> instead.  
  If you are uncertain whether your issue is related to a security vulnerability, just contact the email address
  specified above.
- Search for existing issues and PRs before creating your own.
- Before starting to work on rather bigger changes, additions, or bug fixes, open an appropriate issue and in it
  mention, that you'd like to implement the according solution.
- The repository is currently managed only by myself. Keep that in mind when waiting for an answer. I try to respond as
  quickly as possible. Usually, that takes **around** one day, but there may be instances, in which it takes more. Be
  assured, that I will write back after all.

### Issues

Issues should be used to report bugs, request a new feature, or discuss potential changes before a PR is created. When
you create a new issue, a template will be loaded that will guide you through collecting and providing the required
information.

If you find an issue that is of your interest, please add your feedback to the existing issue rather than creating a new
one. Adding a [reaction](https://github.blog/2016-03-10-add-reactions-to-pull-requests-issues-and-comments/) can also
help by indicating collaborators and other contributors that a particular issue is affecting more people than just the
reporter.

### Pull Requests

Pull Requests are always welcome and a great way to your changes and fixes included in the next release.

This project follows the ["fork-and-pull" Git workflow](https://github.com/susam/gitpr). This includes:

- Updating your fork by pulling from upstream.
- Updating your feature branch by rebasing onto the updated master branch.
- Rebasing the feature branch before submitting and merging PRs.

Here is a quick list of things your Pull Request should do:

- Increase the version numbers in `build.gradle` under the effected module to the new version that this Pull Request
  would represent. The [versioning scheme](#versioning-scheme) Downlomatic uses is [SemVer](http://semver.org/).
- Only fix/add the functionality in question
- Pass a thorough [testing procedure](#testing)
- Include [documentation](#documentation) via JavaDoc/KDoc
- Be accompanied by a filled-out Pull Request template (is loaded automatically)

Although it's not always necessary to open an issue for your PR, you should search for already existing issues
describing the problem you want to fix or additions you want to make. Let other people know that you're working on it by
leaving a quick comment under the appropriate issue.

#### Setup

As Downlomatic's code is primarily written in Kotlin, I recommend
using [JetBrains IntelliJ IDEA](https://www.jetbrains.com/idea/)
because it allows you to seamlessly import your cloned fork. If you manage to make it work in NetBeans or Eclipse, you
are very well allowed to use that IDE, but there's no reason not to use IntelliJ, especially since its built-in code
style and custom run configurations are used for this repository.

### Adding Hosts

The most common contribution is implementing more hosts. Hosts are crawled for videos by the server. The code
implementation tells the server how to do that.

Unfortunately not all websites are technically possible to implement. The following guidelines help you find a valid
page:

- [ ] Access to video may not be protected by reCaptcha
- [ ] Video may not be a "blob:" link or torrent download (only HTTP protocol supported at the moment)
- [ ] Website should not be protected by Cloudflare DDoS protection (while not ideal, it's still possible to implement
  such a website)

To add support for a specific website/host, all you have to do is extending the abstract `Host` class with a no argument
constructor. How to do that and what to watch out for is documented with KDoc. You can also inspect existing
implementations.

The resulting subclass must reside inside the `eu.timerertim.downlomatic.hosts` package in the server module. You should
name that class after the domain of the host (e.g. "animetoast.com" results in a class name of "AnimetoastCom").

## Conventions

### Code Style

Downlomatic uses the **IntelliJ built-in code style**. Unfortunately, this code style is only available in IntelliJ
IDEA.  
You can't use that IDE? No problem, as long as your contribution is properly formatted. Collaborators will reformat the
whole project code before releasing a new version, so we can all enjoy a throughout consistent and well-formatted code
style.

### Documentation

Everything that is public or protected (even classes) should be documented via JavaDoc/KDoc according to these
guidelines
[JavaDoc coding standards](https://blog.joda.org/2012/11/javadoc-coding-standards.html). Public fields or constants do
not have to be documented if their purpose and function are self-explanatory. You are also encouraged to document
private and package-private methods and classes.  
Doing so enables other developers and maybe even yourself to easily make use of the functionality you created. They will
thank you for that.

### Git and Commit Messages

All interactions with repositories on GitHub happen with - well, you guessed it - Git. So you should familiarize
yourself with that VCS before starting to work on your PR. A great website to learn Git, especially for newcomers, is
<https://learngitbranching.js.org>. Furthermore, here is a blog
post [Version Control Best Practices](https://www.git-tower.com/blog/version-control-best-practices/)
covering good practices regarding version control in general.

Consult this blog post [How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/) if you are not sure
how to write good commit messages. This may sound trivial, but is quite important and requires some knowledge. There is
only one further guideline other than the ones mentioned in that blog post: To reference anything GitHub related (
issues, PRs, etc) do not use their GitHub IDs, but a link/URL.  
However, you may want to prefix unfinished or work in progress commits with "**WIP:** " to indicate their state of
completion. This is useful if you have to switch devices but want to save and synchronize your work. Just amend the
following WIP commits. The final and complete commit should also be amended, but remove the "WIP: " prefix from the
commit message.

### Versioning Scheme

The version of this project is defined in the `build.gradle` file. The scheme used is [SemVer](http://semver.org/).
Please update the version in the according module before submitting your Pull Requests. You may find, that the current
version has a suffix indicating pre-releases. If that's the case, leave the suffix as is and only change the major,
minor, and patch number.

If you already know what kind of change you will make (bug fix, backward compatible, or breaking) you can use that to
figure out whether to open an issue before you submit a PR. Incrementing

- Patch version -> Not necessary to open issue
- Minor version -> Most likely necessary (if it's only a small addition, you do not have to)
- Major version -> Necessary

Even though it's not always necessary, you can very well create issues before submitting your PR.

## Testing

Always test your code before submitting Pull Requests and make sure any changes or additions you make actually work.
Write your tests in the test module under `src/test`. Tests should be named after the class they test appended with
"**Test**" and be placed in the same package as the tested class. Create that package if it doesn't exist already.

The project provides JUnit for automated testing. You can use this library or test manually. Either way, be confident
the code works and doesn't break anything.

If you happen to discover something is breaking your code after already submitting a Pull Request, don't worry. Just be
transparent about it and let others know by commenting under your PR before it gets merged. After fixing the problem
notify people by commenting again, and your contribution can finally be integrated. If it has already been merged, just
create a bug report explaining the situation, and we will handle this together.

## Code of Conduct

Our [Code of Conduct](CODE_OF_CONDUCT.md) means that you are responsible for treating everyone on the project with
respect and courtesy regardless of their identity. If you are the victim of any inappropriate behavior or comments as
described in our Code of Conduct, we are here for you and will do the best to ensure that the abuser is reprimanded
appropriately, per our code.

## License

Downlomatic is licensed under the [AGPL-3.0](LICENSE). By contributing you agree to your contribution being granted the
same license.

## Your Recognition

First of all, thank you for contributing to this project. You will now be listed as a contributor and everyone can see,
that thanks to you Downlomatic is what it is today.

Contributors, who greatly impacted Downlomatic's development, will be mentioned under
the [Credits section](README.md#credits). They are also able to become a Collaborator and thus manage this repository.

## Help

If you ever need help, you can ask me directly at <timerertim@gmail.com>. Depending on the growth of the project a
Discord server may be created in the future. It will be linked here once that's done. You will be able to also ask your
questions in the appropriate channel there.