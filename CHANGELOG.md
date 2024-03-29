# Changelog

## [Unreleased]

## [0.1.0] - 2024-02-02

### Fixed

- Removed usage of `org.apache.commons.codec.binary.Base64.decodeBase64(byte[])`, which is
  incompatible with Android.

### Changed

- Switched to Gradle.
- Internal dependency version bumps.

## [0.0.5] - 2022-09-23

### Added

- The new `idempotencyKey` field in `UserEventInput`.

### Changed

- Internal dependency version bumps.

## [0.0.4] - 2022-03-10

### Added

- The new `preserveEmptyAccount` option in `DeleteUserInput`.

### Changed

- The `@Beta` methods `DeleteUserInput.isDoNotTrack` and `DeleteAccountInput.isDoNotTrack` have been
  deprecated in favour of `DeleteUserInput.getDoNotTrack` and `DeleteAccountInput.getDoNotTrack` and
  will be removed in a future release.
- Internal dependency version bumps.

## [0.0.3] - 2021-06-14

### Changed

- Upgraded Apache HttpClient to version 5.1 among other version bumps.

## [0.0.2] - 2021-04-16

### Added

- Methods including `deleteUser`, `deleteAccount`, `blockUser`, `unblockUser`, and some internal
  analytics endpoints for widgets.

### Changed

- The original `@Beta` method `applyReferralCode(String, String, String, RequestOptions)` has been
  deprecated in favour of `applyReferralCode(ApplyReferralCodeInput, RequestOptions)`, and will be
  removed in a future release.

## [0.0.1] - 2021-02-17

[Unreleased]: https://github.com/saasquatch/saasquatch-java-sdk/compare/0.1.0...HEAD

[0.1.0]: https://github.com/saasquatch/saasquatch-java-sdk/compare/0.0.5...0.1.0

[0.0.5]: https://github.com/saasquatch/saasquatch-java-sdk/compare/0.0.4...0.0.5

[0.0.4]: https://github.com/saasquatch/saasquatch-java-sdk/compare/0.0.3...0.0.4

[0.0.3]: https://github.com/saasquatch/saasquatch-java-sdk/compare/0.0.2...0.0.3

[0.0.2]: https://github.com/saasquatch/saasquatch-java-sdk/compare/0.0.1...0.0.2

[0.0.1]: https://github.com/saasquatch/saasquatch-java-sdk/releases/tag/0.0.1
