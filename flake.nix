{
  description = "A basic flake";

  inputs = {
    nixpkgs.url     = "github:nixos/nixpkgs";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = inputs@{ self, nixpkgs, flake-utils, ... }:
  flake-utils.lib.eachDefaultSystem (system:
  let
    overlays       = [ ];
    pkgs           = import nixpkgs {inherit system overlays;};
    additionalPkgs = with pkgs; [  ];
    libXxf86vm     = pkgs.xorg.libXxf86vm;
    openal         = pkgs.openal;
    mesa           = pkgs.mesa;
    libXcursor = pkgs.xorg.libXcursor;
    libXrandr = pkgs.xorg.libXrandr;
    libGL     = pkgs.libGL;
    buildInputs    = with pkgs; 
      [ gradle_6 
        openjdk8-bootstrap 
        xorg.libXxf86vm 
        openal 
        mesa 
        libXcursor 
        libXrandr 
        glxinfo 
        libGL
      ]; 
    
    sharedLibraries = 
      [ libXxf86vm
        openal
        libGL
        libXrandr
        libXcursor
        mesa
      ];

    project = pkgs.stdenv.mkDerivation {
      inherit buildInputs libXxf86vm openal mesa libXcursor libGL sharedLibraries; 
      name        = "flake-env";
      root        = self;
      shellInputs = additionalPkgs;
      shellHook   = 
        (pkgs.lib.strings.concatMapStrings 
         (lib : "export LD_LIBRARY_PATH=${lib}/lib:$LD_LIBRARY_PATH;") 
         sharedLibraries);
      # shellHook = ''
      #     export LD_LIBRARY_PATH=${libXxf86vm}/lib:$LD_LIBRARY_PATH
      #     export LD_LIBRARY_PATH=${openal}/lib:$LD_LIBRARY_PATH
      #     export LD_LIBRARY_PATH=${libGL}/lib:$LD_LIBRARY_PATH
      #     export LD_LIBRARY_PATH=${libXrandr}/lib:$LD_LIBRARY_PATH
      #     export LD_LIBRARY_PATH=${libXcursor}/lib:$LD_LIBRARY_PATH
      #     export LD_LIBRARY_PATH=${mesa}/lib:$LD_LIBRARY_PATH
      #   '';
    };

  in {
  packages.default  = project; # default package
  devShells.default = project; # dev shell
  });
}
