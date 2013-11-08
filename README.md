# cjdnsadmin-clj

A Clojure library for talking to a CJDNS server. Alpha. YMMV.

## Usage

Add to your project.clj :dependencies list:

    [cjdnsadmin-clj "0.1.0-SNAPSHOT"]
    
Then in your .clj file:

    (:require  [cjdnsadmin-clj.core :as cjd :only [request]])

Now you can pass it requests:

    (cjd/request "InterfaceController_peerStats")

As a bonus, it includes a utility function to change host public keys into IPv6 addresses:

    (:require [cjdnsadmin-clj.util :as cjdutil :only [public-to-ip6]])
    (cjdutil/public-to-ip6 pubkey)

## License

Copyright © 2013 Robbie Huffman

Distributed under the Eclipse Public License, the same as Clojure.
