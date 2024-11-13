let
  nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/tarball/nixos-unstable";
  pkgs = import nixpkgs { config = {}; overlays = []; };

  jdk = pkgs.jdk21;

in

pkgs.mkShellNoCC {

  packages = with pkgs; [
    jdk 
  ];
}
