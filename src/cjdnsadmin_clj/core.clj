(ns cjdns-clj.core
  (:import  (java.net InetAddress DatagramPacket DatagramSocket ) )
  (:require 
     [clojure.data.json :as json]
     [bencoding-clj.core :as benc :only [encode decode]]
     [digest :only [sha-256]]
     )
  )

(def cjdns-admin-conf-file (str(System/getProperty "user.home") "/.cjdnsadmin") )
(def cjdns-conf (json/read-json (slurp cjdns-admin-conf-file)))
(def cjdns-host (InetAddress/getByName (:addr cjdns-conf)))
(def cjdns-port (:port cjdns-conf))
(def cjdns-pass (:password cjdns-conf))

(def rcvbuf (atom (clojure.lang.PersistentQueue/EMPTY)))

(def skt (new DatagramSocket))

(def rcv true)
(defn rcvloop []
;  (println rcv)
  (while rcv
    (let [rcvpkt (new DatagramPacket (byte-array 69632) 69632)]
      (.receive skt rcvpkt)
      (swap! rcvbuf conj (new String (.getData rcvpkt) 0 (.getLength rcvpkt)))) ))
(def rcvthread (Thread. rcvloop))
(.start rcvthread)

(defn getresponse []
  (let [response (peek @rcvbuf)]
    (swap! rcvbuf pop )
    response))
(defn checkresponse []
  (if (peek @rcvbuf) true false))

(defn send-req [req] 
    (.send skt
     (apply #(new DatagramPacket (. % getBytes) (. % length) cjdns-host cjdns-port)  [req])))

(defn ping [] 
    (.send skt
     (apply #(new DatagramPacket (. % getBytes) (. % length) cjdns-host cjdns-port)  ["d1:q4:pinge"])))

(defn adminfunc [] 
    (.send skt
     (apply #(new DatagramPacket (. % getBytes) (. % length) cjdns-host cjdns-port)  ["d4:argsd4:pagei0ee1:q24:Admin_availableFunctionse"])))

(defn peerstats [] 
    (.send skt
     (apply #(new DatagramPacket (. % getBytes) (. % length) cjdns-host cjdns-port)  ["d1:q29:InterfaceController_peerStatse"]  )))

(defn cookie [] 
    (.send skt
     (apply #(new DatagramPacket (. % getBytes) (. % length) cjdns-host cjdns-port)  ["d1:q6:cookiee"]  )))

(def cookie-atom (atom ""))
(defn get-cookie [] 
  (cookie) 
  (while (not (checkresponse))  (Thread/sleep 100))
  (let [response (get  (first  (benc/decode (getresponse)) ) "cookie")]
    (reset! cookie-atom response)))

(defn hash-pass []  (digest/sha-256 (str cjdns-pass @cookie-atom)))
(defn request [q & args]
  (get-cookie)
  (let [req     (if args {"q" "auth", "aq" q, "hash" (hash-pass), "cookie" @cookie-atom, "args" (first args)} {"q" "auth", "aq" q, "hash" (hash-pass), "cookie" @cookie-atom} )
        req1    (benc/encode req)
        cp-hash (digest/sha-256 req1)
        req2    (benc/encode  (assoc req "hash" cp-hash))
        ]
;    (println @cookie-atom)
;    (println (benc/decode req1))
;    (println (benc/decode req2))
    (send-req req2)
    (while (not (checkresponse))  (Thread/sleep 100))
    (let [resp    (getresponse)
          decoded (benc/decode resp)
          ]
      decoded
      )
    ) 
  )


(comment 
(request "Admin_availableFunctions" ) 
(request "ping" ) 
(pprint (request "Admin_availableFunctions" {"page" 2})) 
(request "Admin_availableFunctions" {"page" 3}) 
(benc/decode (request "InterfaceController_peerStats" {"page" 1})) 
(def broken (request "InterfaceController_peerStats" ))
  (pprint broken)
  (pprint (benc/decode broken))
(get-in  (first (request "InterfaceController_peerStats" )) ["peers"])
(request "InterfaceController_peerStats" {"page" 1}) 
(request "memory" ) 
(request "AuthorizedPasswords_list" ) 
(request "NodeStore_dumpTable" {"page" 1})
(request "RouterModule_pingNode" {"path" "fce3:b909:def1:aa07:2efa:4314:e83e:c3dc" } ) 
(benc/decode (request "RouterModule_pingNode" {"path" "fce3:b909:def1:aa07:2efa:4314:e83e:c3dc" } )) 
  
(benc/encode {"q" "InterfaceController_peerStats" "args" {"page" 0}})
(benc/decode "d4:argsd4:pagei0ee1:q24:Admin_availableFunctionse")
(benc/encode {"q" "ping"})
(benc/encode {"q" "cookie"})
(digest/sha-256 "this")
(pprint cjdns-conf)
(.getState rcvthread)
(swap! rcvbuf conj "Test")
(swap! rcvbuf pop )
(peek @rcvbuf) 
(ping) (getresponse)
(do (adminfunc) (pprint (benc/decode (getresponse))))
(do (peerstats) (pprint (benc/decode (getresponse))))
  (checkresponse)
(pprint (benc/decode (getresponse)))
(get-cookie) 
(let  [req     {"q" "auth", "aq" "ping", "hash" (hash-pass), "cookie" @cookie-atom }
       req1    (benc/encode req)
       cp-hash (digest/sha-256 req1)
       req2    (benc/encode  (assoc req "hash" cp-hash)) ]
;  (println req1)
  (try  (pprint (benc/decode req1)))
  (send-req req2)
  (pprint (benc/decode (getresponse)))
  )
;(println rcvbuf)
)

