# lein-m2-clean-old-artifacts

Leiningen plugin intended for removing unused artifacts.

It uses atime unix file attribute in order to determine when given artifact was last accessed.
If atime value is lower then current time minus specified number of days, artifact is deleted (unless `--dry-run option is used`).

Since regular Clojure/Java development can accumulate quite a lot of those artifacts, having a quick method to perform cleanup from time to time can be quite handy.

## Installation


Put `[lein-m2-clean-old-artifacts "0.1.0"]` into the `:plugins` vector of your `:user` profile.

For instance:

    {:user {:plugins [[lein-localrepo "0.1.0"]]}}
    
Plugin was tested only with Leiningen 2.x on unix-like systems.

## Usage

Usage: 

    lein m2-clean-old-artifacts [--m2-artifacts-directory=DIRECTORY] [--days=DAYS] [--dry-run]

Options:
  --m2-artifacts-directory=DIRECTORY
    Sets directory containing Maven artifacts.
    Defaults to ~/.m2.

  --days=DAYS
    Sets number of days, that make artifact old. 
    Any jar file which was last accessed earlier then current time minus specified days, will be considers as old by this plugin.
    Defaults to 365.

  --dry-run
    Does not delete found artifacts, only lists directories that contain them.  

## TODO

1. Tests
2. Cleanup of empty directories.

## License

Copyright © 2018 Kamil Szymczyk

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
