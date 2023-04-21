# Notes Mk II

Notes Mk II is a small note-taking app for Android. It uses Android's
[Storage Access Framework](https://developer.android.com/guide/topics/providers/document-provider)
to read and write plain text files stored by any document storage provider on
your device (local or cloud-based storage).


## Features

- Requires no permissions
- Small app (< 32 KB)
- No third-party runtime dependencies


## Build

Run the following command from the root of the repository to make a debug
build:

```shell
./gradlew assembleDebug
```

Making a release build is similar, but requires environment variables to be set
to indicate how to sign the APK:

```shell
STORE_FILE='...' STORE_PASSWORD='...' KEY_ALIAS='...' KEY_PASSWORD='...' ./gradlew assembleRelease
```


## License

```
Copyright 2021 Rashad Sookram

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
