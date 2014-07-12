(ns fn-jenkins.jobs.utility-operations)

(defn path-for
  "Get the actual filename corresponding to a template."
  [base] (str "crate/jenkins/" base))

(defn truefalse [value]
  (if value "true" "false"))

(defn url-without-path
  [url-string]
  (let [url (java.net.URL. url-string)]
    (java.net.URL. (.getProtocol url) (.getHost url) (.getPort url) "")))
