{
  description = "BB Launcher — BlackBerry OS 7 home screen for Zinwa Q25";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.allowUnfree = true;
          config.android_sdk.accept_license = true;
        };

        androidComposition = pkgs.androidenv.composeAndroidPackages {
          buildToolsVersions = [ "35.0.0" ];
          platformVersions = [ "34" "35" ];
          includeEmulator = false;
          includeSources = false;
          includeNDK = false;
        };

        androidSdk = androidComposition.androidsdk;
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = [
            pkgs.jdk17
            androidSdk
            pkgs.gradle

            # Firmware icon extraction tools
            pkgs.p7zip        # Extract .exe self-extracting archives
            pkgs.unshield     # Extract InstallShield .cab archives
            pkgs.imagemagick  # Identify, convert, resize PNGs
            pkgs.file         # Identify file types in extracted blobs
          ];

          # Point Gradle/AGP at the Nix-managed SDK
          ANDROID_HOME = "${androidSdk}/libexec/android-sdk";
          ANDROID_SDK_ROOT = "${androidSdk}/libexec/android-sdk";
          JAVA_HOME = "${pkgs.jdk17}";

          shellHook = ''
            echo "BB Launcher dev shell ready"
            echo "  Java: $(java -version 2>&1 | head -1)"
            echo "  ANDROID_HOME: $ANDROID_HOME"
          '';
        };
      });
}
