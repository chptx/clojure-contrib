;; Generic interfaces for arithmetic operations

;; by Konrad Hinsen
;; last updated March 19, 2009

;; Copyright (c) Konrad Hinsen, 2009. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns clojure.contrib.generic.arithmetic
  "Generic arithmetic interface

   NOTE: This library is VERY experimental. It WILL change significantly
   with future release.

   This library defines generic versions of + - * / as multimethods
   that can be defined for any type. The minimal required implementations
   for a type are binary + and * plus unary - and /. Everything else
   is derived from these automatically. Explicit binary definitions
   for - and / can be provided for efficiency reasons."
  (:use [clojure.contrib.generic
	 :only (root-type nulary-type nary-type nary-dispatch)]
	[clojure.contrib.types :only (defadt)])
  (:refer-clojure :exclude [+ - * /]))

;
; Universal zero and one values
;
(defadt ::zero zero)
(defadt ::one one)

(derive ::zero root-type)
(derive ::one root-type)

;
; Addition
;
; The minimal implementation is for [::binary my-type]. It is possible
; in principle to implement [::unary my-type] as well, though this
; doesn't make any sense.
;
(defmulti + nary-dispatch)

(defmethod + nulary-type
  []
  zero)

(defmethod + root-type
  [x] x)

(defmethod + [root-type ::zero]
  [x y] x)

(defmethod + [::zero root-type]
  [x y] y)

(defmethod + nary-type
  [x y & more]
  (if more
    (recur (+ x y) (first more) (next more))
    (+ x y)))

;
; Subtraction
;
; The minimal implementation is for unary my-type. A default binary
; implementation is provided as (+ x (- y)), but it is possible to
; implement unary my-type explicitly for efficiency reasons.
;
(defmulti - nary-dispatch)

(defmethod - nulary-type
  []
  (throw (java.lang.IllegalArgumentException.
	  "Wrong number of arguments passed")))

(defmethod - [root-type ::zero]
  [x y] x)

(defmethod - [::zero root-type]
  [x y] (- y))

(defmethod - [root-type root-type]
  [x y] (+ x (- y)))

(defmethod - nary-type
  [x y & more]
  (if more
    (recur (- x y) (first more) (next more))
    (- x y)))

;
; Multiplication
;
; The minimal implementation is for binary [my-type my-type]. It is possible
; in principle to implement unary my-type as well, though this
; doesn't make any sense.
;
(defmulti * nary-dispatch)

(defmethod * nulary-type
  []
  one)

(defmethod * root-type
  [x] x)

(defmethod * [root-type ::one]
  [x y] x)

(defmethod * [::one root-type]
  [x y] y)

(defmethod * nary-type
  [x y & more]
  (if more
    (recur (* x y) (first more) (next more))
    (* x y)))

;
; Division
;
; The minimal implementation is for unary my-type. A default binary
; implementation is provided as (* x (/ y)), but it is possible to
; implement binary [my-type my-type] explicitly for efficiency reasons.
;
(defmulti / nary-dispatch)

(defmethod / nulary-type
  []
  (throw (java.lang.IllegalArgumentException.
	  "Wrong number of arguments passed")))

(defmethod / [root-type ::one]
  [x y] x)

(defmethod / [::one root-type]
  [x y] (/ y))

(defmethod / [root-type root-type]
  [x y] (* x (/ y)))

(defmethod / nary-type
  [x y & more]
  (if more
    (recur (/ x y) (first more) (next more))
    (/ x y)))

;
; Macros to permit access to the / multimethod via namespace qualification
;
(defmacro defmethod*
  [ns name & args]
  (let [qsym (symbol (str ns) (str name))]
    `(defmethod ~qsym ~@args)))

(defmacro qsym
  [ns sym]
  (symbol (str ns) (str sym)))

;
; Minimal implementations for java.lang.Number
;
(defmethod + [java.lang.Number java.lang.Number]
  [x y] (clojure.core/+ x y))

(defmethod - java.lang.Number
  [x] (clojure.core/- x))

(defmethod * [java.lang.Number java.lang.Number]
  [x y] (clojure.core/* x y))

(defmethod / java.lang.Number
  [x] (clojure.core// x))

