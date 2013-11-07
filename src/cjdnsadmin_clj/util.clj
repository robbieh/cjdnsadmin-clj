(ns cjdnsadmin-clj.util
  (:import 
     [org.apache.commons.codec.digest DigestUtils]
     )
  )




; Adapted directly from cjdns source

(def numForAscii [
                          99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,
                          99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,
                          99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,99,
                           0, 1, 2, 3, 4, 5, 6, 7, 8, 9,99,99,99,99,99,99,
                          99,99,10,11,12,99,13,14,15,99,16,17,18,19,20,99,
                          21,22,23,24,25,26,27,28,29,30,31,99,99,99,99,99,
                          99,99,10,11,12,99,13,14,15,99,16,17,18,19,20,99,]
)

(defn ubyte  [val]
     (if  (>= val 128)
            (byte  (- val 256))
            (byte val)))


(defn base32-decode [s]
  (let [outarr (int-array (.length s))
        outputIndex (atom 0)
        inputIndex (atom 0)
        nextByte (atom (byte 0))
        bits (atom 0)
        ]
;    (String. (byte-array (map (comp byte) outarr)) "UTF-8")
    (while (< @inputIndex (.length s))
      (let [o (java.lang.Character/codePointAt s (int @inputIndex) )]
;        (println "codepoint: " o )
    ;    (println (aget outarr 0)(aget outarr 1)(aget outarr 2)(aget outarr 3)(aget outarr 4))
        (if (not (= 0 (bit-and o 0x80))) (throw (Exception. (str "bad value: " o " char "(char o)) )))
        (let [b (get numForAscii o)]
;          (println "b: " b)
          (swap! inputIndex inc)
          (if (> b 31) (throw (Exception. (str "bad character " @inputIndex ))))
          (reset! nextByte (bit-or @nextByte (bit-shift-left (byte b) @bits)))
          (reset! bits (+ @bits 5))) 
        (if (>= @bits 8) (do
;                           (println "   outarr: " (str outarr))
;                           (println "   nextByte: " @nextByte (type @nextByte))
;                           (println "   bit-and: " (bit-and @nextByte 0xff))
                           (aset-int outarr @outputIndex (bit-and 0xff @nextByte))
;                           (println "   outarr: " (str outarr))
;                           (println "   outputIndex: "@outputIndex)
                           (swap! outputIndex inc)
;                           (println "   outputIndex: "@outputIndex)
;                           (println "   bits: " @bits)
                           (reset! bits (- @bits 8))
;                           (println @bits)
;                           (println "   nextByte: " @nextByte)
                          (reset! nextByte (bit-shift-right @nextByte 8))
;                           (println @nextByte)
                          )) 
        ))
;      (if (or (>= @bits 5) @nextByte) (throw (Exception. "bits bytes bad TODO")))
    
   ;(println (String. (byte-array (map byte (take @outputIndex (amap ^ints outarr idx ret (- (int 128) (aget ^ints outarr idx))))))))
    ;(String.  (byte-array (take @outputIndex outarr)))
   (byte-array (map ubyte (int-array  (take @outputIndex outarr)))) 
;     (int-array (take @outputIndex (amap ^ints outarr idx ret (bit-and 0xff  (aget ^ints outarr idx)))))
;    (byte-array  (map byte (take @outputIndex outarr)))
;    (byte-array (map byte (take @outputIndex (amap ^ints outarr idx ret (bit-and 0xff (aget ^ints outarr idx))))))
;    (int-array (map int (take @outputIndex (amap ^ints outarr idx ret (- (int 128) (aget ^ints outarr idx))))))
;    (byte-array (map byte (take @outputIndex (amap ^bytes outarr idx ret (bit-and 0xff  (aget ^bytes outarr idx))))))

    )
  )

(defn public-to-ip6 [pubkey]
  (let [pubkey (.substring pubkey 0 (-  (.length pubkey) 2))
        digest (-> pubkey .toUpperCase  base32-decode DigestUtils/sha512 DigestUtils/sha512Hex)
        ip6    (apply str (interpose ":" (map #(apply str %) (partition 4 (take 32 digest)))))
        ]
    ip6) )

