(ns cjdns-clj.util
  (:import 
 ;    [java.security MessageDigest]
 ;    [java.math.BigInteger]
;     [unsuck.io CrockfordBase32]
;     [org.xbill.DNS.utils base32]
     [org.apache.commons.codec.binary Base32]
     [org.apache.commons.codec.digest DigestUtils]
     )
  (:require 
;     [digest :only [sha-512]]
;     [base32-clj.core :as b32 :only [decode]]
     [clojure.string :only [join]
      
      ]
     )
  )


(comment------------


(let [b32 (new Base32)
      pubkey (.toUpperCase "hy7fhbsbkcvddd8sn7b2pvvmg01b6y5w97mks6x94llkvnzlhk40")]
  (b32/decode pubkey)
------------)  

 
(let [s5 (DigestUtils/getSha512Digest)
      
      ]
  (println s5)
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


(defn base32-decode [s]
  (let [outarr (int-array (.length s))
        outputIndex (atom 0)
        inputIndex (atom 0)
        nextByte (atom (byte 0))
        bits (atom 0)
        ]
    (String. (byte-array (map (comp byte) outarr)) "UTF-8")
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
                           (aset-int outarr @outputIndex (bit-and @nextByte 0xff))
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
;    (int-array  (take @outputIndex outarr)) 
;     (int-array (take @outputIndex (amap ^ints outarr idx ret (bit-and 0xff  (aget ^ints outarr idx)))))
;    (byte-array  (map byte (take @outputIndex outarr)))
;    (byte-array (map byte (take @outputIndex (amap ^ints outarr idx ret (- (int 127) (aget ^ints outarr idx))))))
    (int-array (map int (take @outputIndex (amap ^ints outarr idx ret (- (int 128) (aget ^ints outarr idx))))))
;    (byte-array (map byte (take @outputIndex (amap ^bytes outarr idx ret (bit-and 0xff  (aget ^bytes outarr idx))))))
    )
  )

(comment ------------
;(java.lang.Character/codePointAt "9" 0)
;(bit-and 57 0x80)
;(get numForAscii 57)
(byte (bit-and 0xff 207))
(defn cpa [x] (java.lang.Character/codePointAt x 0))
(defn hexdigest [input])
(println "sha512 of abcdefghijklmnopqrstuvwxyz" (digest/sha-512 "abcdefghijklmnopqrstuvwxyz"))
(println "decoded: " (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40"))
(println "decoded: " (base32-decode "hy7fhbsbkcvddd8sn7b2pvvmg01b6y5w97mks6x94llkvnzlhk40"))
(println "decoded: " (CrockfordBase32/decode "hy7fhbsbkcvddd8sn7b2pvvmg01b6y5w97mks6x94llkvnzlhk40"))
(println "decoded type: " (type (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40")))
(println "dehashed: " (digest/sha-512 (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40")))
(cpa 0x80)
(CrockfordBase32/decode "MY======")
(defn xbill-decode [s] (.fromString  (new org.xbill.DNS.utils.base32 "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567=" false false) s) )
(defn hexdigest [ba]
  (let [md (MessageDigest/getInstance "SHA-512")]
    (. md update ba)
    (let [digest (.digest md)
          bihash (BigInteger. 1 digest)
          hd     (.toString bihash 16)]
      hd)))
(defn digest [ba]
  (let [md (MessageDigest/getInstance "SHA-512")]
    (. md update ba)
    (let [digest (.digest md) ]
      digest)))
(let [keyBytes (xbill-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40") ]
  (println (type keyBytes))
  (println (take 5 keyBytes))
  (let [d (digest keyBytes)
        hd (hexdigest d)
        ]
    (println d)
    (println hd)
    )
)
(let [keyInts (base32-decode "hy7fhbsbkcvddd8sn7b2pvvmg01b6y5w97mks6x94llkvnzlhk40")
      keyBytes (byte-array (map byte keyInts)) 
      keyBytes (xbill-decode "hy7fhbsbkcvddd8sn7b2pvvmg01b6y5w97mks6x94llkvnzlhk40")
      ]
  (println (type keyInts))
  (println (take 5 keyInts))
  (println (type keyBytes))
  (println (take 5 keyBytes))
  (let [d (digest keyBytes)
        hd (hexdigest d)
        ]
    (println d)
    (println hd)
    )
)
(let [keyInts (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40")
      keyBytes (byte-array (map byte keyInts))
      md (MessageDigest/getInstance "SHA-512") ]
  (println (type keyInts))
  (println (take 5 keyInts))
  (println (type keyBytes))
  (println (take 5 keyBytes))
  (. md update keyBytes)
    (let [digest (.digest md)
          bihash (BigInteger. 1 digest)
          hd     (.toString bihash 16)
          ]
      (println bihash)
      (println hd)
      (println (take 5 digest))
      (println (String. digest))
      (println (clojure.string/join "" (map #(Integer/toHexString (bit-and % 0xff)) digest)))
      )
  )
(let [keyBytes (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40")
      md (MessageDigest/getInstance "SHA-512")]
  (println (type keyBytes))
  (println (take 5 keyBytes))
  (println (nth keyBytes 20))
  (println md)
  (println (byte-array (type keyBytes)))
  (. md update keyBytes)
  (println md)
    (let [digest (.digest md)
;          bihash (new BigInteger 1 md)
;          hd     (.toString bihash 16)
          ]
       (println (type digest))
;      (println "bihash: " bihash)
      ;(println "hd: " hd)
      (clojure.string/join "" (map #(Integer/toHexString (bit-and % 0xff)) digest))
      )
  )
;(println "dehashed: " (apply str (map #(format "%02x" (bit-and % 0xff)) (digest/sha-512 (digest/sha-512 (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40"))))))
;(println "dehashed: " (digest/sha-512 (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40")))
;(println "dehashed: " (digest/sha-512 (digest/sha-512 (base32-decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40"))))
(defn pubKeyToIp6 [pubkey]
  ;(b32/decode "428dhuggpl58sk0k0q2xr3sswfmr3phdr307ysv0mpcg143wdkq0") 
  (.length "428dhuggpl58sk0k0q2xr3sswfmr3phdr307ysv0mpcg143wdkq0") 
  ;(b32/encode "test")
  ;(b32/decode "KR3WC4ZAMJZGS3DMNFTSYIDBNZSCA5DIMUQHG3DJORUHSIDUN53GK4Y=")

  )
(def peers '({"switchLabel" "0000.0000.0000.0013", "bytesOut" 2230252605, "lostPackets" 2173, "duplicates" 0, "bytesIn" 2353395348, "isIncoming" 0, "state" "ESTABLISHED", "last" 1383514093600, "publicKey" "hy7fhbsbkcvddd8sn7b2pvvmg01b6y5w97mks6x94llkvnzlhk40.k", "receivedOutOfRange" 0}  {"switchLabel" "0000.0000.0000.0015", "bytesOut" 3223197779, "lostPackets" 10, "duplicates" 0, "bytesIn" 3566702288, "isIncoming" 0, "state" "ESTABLISHED", "last" 1383514093569, "publicKey" "x2d5l3jpvyzz730cd86yxbx615kslmvwxk35mtlzvpbmmq30gsv0.k","receivedOutOfRange" 0}  {"switchLabel" "0000.0000.0000.0017", "bytesOut" 697205630, "lostPackets" 225, "duplicates" 0, "bytesIn" 625650839, "isIncoming" 0, "state" "ESTABLISHED", "last" 1383514093401, "publicKey" "xbjdqz1j5mxhrdylkhv45krjruxuv0193dczdbnvvzywuz3p0xj0.k", "receivedOutOfRange" 0} {"switchLabel" "0000.0000.0000.0019", "bytesOut" 960783119, "lostPackets" 4, "duplicates" 0, "user" "password [1]", "bytesIn" 661695170, "isIncoming" 1, "state" "ESTABLISHED", "last" 1383514092770, "publicKey" "fud8fq45u2wr54cvlzwr7cfctx51yj9gluzpt97tpvjd3d34rmt0.k", "receivedOutOfRange" 0}  {"switchLabel" "0000.0000.0000.001b", "bytesOut" 209434232, "lostPackets" 445, "duplicates" 0, "user" "password [1]", "bytesIn" 195286644, "isIncoming" 1, "state" "ESTABLISHED", "last" 1383514093377, "publicKey" "428dhuggpl58sk0k0q2xr3sswfmr3phdr307ysv0mpcg143wdkq0.k", "receivedOutOfRange" 0}))
;(for [p peers] () (get p "publicKey"))
(def peerkeys '("HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40.K" "X2D5L3JPVYZZ730CD86YXBX615KSLMVWXK35MTLZVPBMMQ30GSV0.K" "XBJDQZ1J5MXHRDYLKHV45KRJRUXUV0193DCZDBNVVZYWUZ3P0XJ0.K" "FUD8FQ45U2WR54CVLZWR7CFCTX51YJ9GLUZPT97TPVJD3D34RMT0.K" "428DHUGGPL58SK0K0Q2XR3SSWFMR3PHDR307YSV0MPCG143WDKQ0.K"))

;(b32/decode "HY7FHBSBKCVDDD8SN7B2PVVMG01B6Y5W97MKS6X94LLKVNZLHK40000")
------------)
