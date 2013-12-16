(defproject cjdnsadmin-clj "0.1.0"
  :description "A Clojure interface to CJDNS admin functions"
  :url "https://github.com/robbieh/cjdnsadmin-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :java-source-paths ["java-src"]
  :dependencies [[org.clojure/data.json "0.2.3"]
                 [org.clojure/clojure "1.4.0"]
                 [bencoding-clj "0.1.0"]
                 [base32-clj "0.1.0"]
                 [digest "1.4.3"]
                 [commons-codec/commons-codec "1.8"]
                 ]
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg }]]
            )
