;;  Copyright (c) Frantisek Sodomka. All rights reserved.  The use and
;;  distribution terms for this software are covered by the Eclipse Public
;;  License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;;  be found in the file epl-v10.html at the root of this distribution.  By
;;  using this software in any fashion, you are agreeing to be bound by the
;;  terms of this license.  You must not remove this notice, or any other,
;;  from this software.
;;
;;  Created 1/29/2009

(ns clojure.contrib.test-clojure.logic
  (:use clojure.contrib.test-is))

;; *** Helper functions ***

(defn exception []
  (throw (new Exception "Exception which should never occur")))


;; *** Tests ***

(deftest test-if
  ;(is (thrown? Exception (if)))        ; !!! ERROR IN CATCHING THIS EXCEPTION
  ;(is (thrown? Exception (if true)))   ; !!! ERROR IN CATCHING THIS EXCEPTION

  ; true/false/nil
  (are (= _1 _2)
    (if true :t) :t
    (if true :t :f) :t
    (if true :t (exception)) :t

    (if false :t) nil
    (if false :t :f) :f
    (if false (exception) :f) :f

    (if nil :t) nil
    (if nil :t :f) :f
    (if nil (exception) :f) :f
  )

  ; zero/empty is true
  (are (= (if _ :t :f) :t)
    (byte 0)
    (short 0)
    (int 0)
    (long 0)
    (bigint 0)
    (float 0)
    (double 0)
    (bigdec 0)

    0/2
    ""
    #""
    (symbol "")

    ()
    []
    {}
    #{}
    (into-array [])
  )

  ; anything except nil/false is true
  (are (= (if _ :t :f) :t)
    (byte 2)
    (short 2)
    (int 2)
    (long 2)
    (bigint 2)
    (float 2)
    (double 2)
    (bigdec 2)

    2/3
    \a
    "abc"
    #"a*b"
    'abc
    :kw

    '(1 2)
    [1 2]
    {:a 1 :b 2}
    #{1 2}
    (into-array [1 2])

    (new java.util.Date)
  )
)

; nil punning:
; (if (rest [9]) 1 2) -> is currently 2, but without nil punning would be 1
; (if (seq (rest [1])) 1 2) -> 2


(deftest test-and
  (are (= _1 _2)
    (and) true
    (and true) true
    (and nil) nil
    (and false) false

    (and true nil) nil
    (and true false) false

    (and 1 true :kw 'abc "abc") "abc"

    (and 1 true :kw nil 'abc "abc") nil
    (and 1 true :kw nil (exception) 'abc "abc") nil

    (and 1 true :kw 'abc "abc" false) false
    (and 1 true :kw 'abc "abc" false (exception)) false
  )
)


(deftest test-or
  (are (= _1 _2)
    (or) nil
    (or true) true
    (or nil) nil
    (or false) false

    (or nil false true) true
    (or nil false 1 2) 1
    (or nil false "abc" :kw) "abc"

    (or false nil) nil
    (or nil false) false
    (or nil nil nil false) false

    (or nil true false) true
    (or nil true (exception) false) true
    (or nil false "abc" (exception)) "abc"
  )
)


(deftest test-not
  (is (thrown? IllegalArgumentException (not)))
  (are (= (not _) true)
    nil
    false
  )
  (are (= (not _) false)
    true

    ; numbers
    0
    0.0
    42
    1.2
    0/2
    2/3

    ; characters
    \space
    \tab
    \a

    ; strings
    ""
    "abc"

    ; regexes
    #""
    #"a*b"

    ; symbols
    (symbol "")
    'abc

    ; keywords
    :kw

    ; collections/arrays
    ()
    '(1 2)
    []
    [1 2]
    {}
    {:a 1 :b 2}
    #{}
    #{1 2}
    (into-array [])
    (into-array [1 2])

    ; Java objects
    (new java.util.Date)
  )
)

