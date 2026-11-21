{
  description = "TimeAppDemo KMP project: reproducible Android dev shell";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    android-nixpkgs.url = "github:tadfisher/android-nixpkgs";
    android-nixpkgs.inputs.nixpkgs.follows = "nixpkgs";
  };

  outputs = { self, nixpkgs, android-nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs { inherit system; };
      androidPkgs = android-nixpkgs.packages.${system};

      # Standard Android SDK layout. compileSdk/targetSdk come from
      # gradle/libs.versions.toml (android-compileSdk = 37).
      androidSdkRoot = pkgs.runCommand "android-sdk-root" {
        nativeBuildInputs = [ pkgs.coreutils ];
      } ''
        mkdir -p $out/platforms/android-37.0
        cp -rL ${androidPkgs."platforms-android-37-0"}/. $out/platforms/android-37.0/
        cp ${./nix/android-37.0-package.xml} $out/platforms/android-37.0/package.xml

        mkdir -p $out/build-tools/37.0.0
        cp -rL ${androidPkgs."build-tools-37-0-0"}/. $out/build-tools/37.0.0/

        mkdir -p $out/build-tools/36.0.0
        cp -rL ${androidPkgs."build-tools-36-0-0"}/. $out/build-tools/36.0.0/

        mkdir -p $out/platform-tools
        cp -rL ${androidPkgs."platform-tools"}/. $out/platform-tools/

        mkdir -p $out/cmdline-tools/latest/bin
        touch $out/cmdline-tools/latest/bin/sdkmanager

        mkdir -p $out/licenses
        echo "24333f8a63b6825ea9c5514f83c2829b004d1fee" > $out/licenses/android-sdk-license
        echo "d56f5187479451eabf01fb78af6dfcb131a6481e" >> $out/licenses/android-sdk-license
        echo "d56f5187479451eabf01fb78af6dfcb131a6481e" > $out/licenses/android-sdk-preview-license
      '';
    in
    {
      devShells.${system}.default = pkgs.mkShell {
        name = "timeappdemo-dev-shell";

        # gradle/gradle-daemon-jvm.properties pins a Java 21 daemon.
        # gcc.cc.lib: Kotlin/Native's konanc env-stubs .so links libstdc++.so.6.
        buildInputs = [
          pkgs.temurin-bin-21
          pkgs.gcc.cc.lib
        ];

        shellHook = ''
          export ANDROID_HOME="${androidSdkRoot}"
          export ANDROID_SDK_ROOT="${androidSdkRoot}"
          export JAVA_HOME="${pkgs.temurin-bin-21}"
          export GRADLE_USER_HOME="$HOME/.gradle"
          export LD_LIBRARY_PATH="${pkgs.gcc.cc.lib}/lib"
          echo "sdk.dir=${androidSdkRoot}" > local.properties
        '';
      };
    };
}
