# clojure_test
Nothing but a counter

## Usage
- Requirements
  - JDK (version used while first building this repo was 21.0.2-open)
  - Clojure (version used while first building this repo was 1.12.1.1550)

### Implementation 1
This implementation has only a front-end written in ClojureScript with a button for incrementing the counter and another one for reseting it to 0. The counter should reset itself to 0 when the page is refreshed.

#### Running
From the root of the repository:
- ```shell
  cd implementation_1/front_end
  ```
- ```shell
  clj -M -m cljs.main -c front-end-counter.core -r
  ```

A REPL session should start and a new tab should pop-up in the browser with the counter in 0 and the two buttons.

### Implementation 2
This implementation has a front-end written in ClojureScript similar to the first implementation, but now it sends HTTP requests for a back-end written in Clojure, and the back-end is in charge of the counter. The counter should keep its value even when the page is refreshed.

#### Running
From the root of the repository:
- ```shell
  cd implementation_2/front_end
  ```
- ```shell
  clj -M -m cljs.main -c front-end-counter.core -r
  ```

As in the first implemetation, a REPL session should start and a new tab should pop-up in the browser.

In this implementation we have to start the back-end as well, so, from the root of this repository:
- ```shell
  cd implementation_2/back_end
  ```
- ```shell
  clojure -M -m counter-server.core
  ```

### Implementation 3
The front-end for this implementation is exactly the same as the one in the second implementation. It also has a Clojure back-end in charge of the counter, but now, the data is persisted into a Datomic database. The counter should keep its value even when the back-end is restarted.

#### Running
From the root of the repository:
- ```shell
  cd implementation_3/front_end
  ```
- ```shell
  clj -M -m cljs.main -c front-end-counter.core -r
  ```

As before, a REPL session should start and a new tab should pop-up in the browser.

In this implementation we can start the back-end in two ways, so, from the root of this repository:
- ```shell
  cd implementation_3/back_end
  ```
- to use the Datomic in-memory storage (included just for test reasons):
  ```shell
   clojure -M -m datomic-counter.core
  ```
- to use the Datomic file storage (the one that fullfils our requirements):
  ```shell
   clojure -J-Ddb=$(pwd) -M -m datomic-counter.core
  ```
  >Here we are using the current directory to put the Datomic database file, but any absolute path should work
