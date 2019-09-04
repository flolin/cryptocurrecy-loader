### Crypto-Compare API ###
https://min-api.cryptocompare.com

You need to get you own apiKey and save it within the app-config files.


### Install Local Gitlab-Runner ###

$ sudo curl --output /usr/local/bin/gitlab-runner https://gitlab-runner-downloads.s3.amazonaws.com/latest/binaries/gitlab-runner-darwin-amd64
$ sudo chmod +x /usr/local/bin/gitlab-runner

https://medium.com/@campfirecode/debugging-gitlab-ci-pipelines-locally-e2699608f4df

Prepare gitlab-ci.yaml

image: node:latest

Build:
  stage: build
  script:
    - docker build .

Unit-Tests:
  stage: test
  script:
    - npm install
    - npm test

Run unit-tests:

$ cd path/to/project
$ gitlab-runner exec docker Unit-Tests
gitlab-runner exec docker Unit-Tests —-docker-volumes /var/run/docker.sock:/var/run/docker.sock

Quirk:
Your local changes must be committed to git or they won’t be available to gitlab-runner.
Because of this, I find it useful to use $ git reset — soft HEAD~1 which will unstage the last commit(s) made when debugging CI issues until the issue is resolved.

