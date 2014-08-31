(comment
(ns crates.tgz-utils
  (:use [clostache.parser :only [render-resource]]
        [landing-site.dev-ops :only [ssh-key-paths]]
        [landing-site.dev-ops-config :only [delivery-settings qa-release-target production-cms]]
        [pallet.api :only [plan-fn node-spec server-spec group-spec lift]]
        [pallet.actions :only [remote-directory directory exec-script* remote-file]]
        [pallet.crate :only [defplan]]))

(defplan deploy-tarball
  "Copy to remote server, gunzip and untar the archive"
  [{:keys [tgz-url remote-dir owner group] :as settings}]
  (server-spec :phases {:configure (plan-fn
                                    (remote-directory remote-dir
                                                      :url tgz-url
                                                      :owner owner
                                                      :group group
                                                      :mode "0644"
                                                      :action :create
                                                      :force true
                                                      :unpack :tar))}))

(defplan rsync-deployed-files
  "keep deployed tarball files upto date with the most recently published version"
  [{:keys [domain-name owner group bin-dir username] :as settings}]
  (let [crontab-entry (str "* * * * *  /home/" username "/bin/rsync-site-cms")
        crontab-edit-cmd (str "crontab -u  " username " -l ; echo  '" crontab-entry "' | crontab -u " username "  -")]
    (server-spec :phases {:settings (plan-fn
                                     (do
                                       (try (exec-script* crontab-edit-cmd) (catch Exception e))
                                       (try (directory bin-dir :action :create :owner username :group username) (catch Exception e))
                                       (try (remote-file
                                        (str bin-dir "/rsync-site-cms")
                                        :owner username :group username :mode "0744"
                                        :action :create :force true
                                        :overwrite-changes true :no-versioning true
                                        :content (render-resource "templates/rsync-cms-site-content.tmpl"
                                                                  {:domain-name domain-name
                                                                   :username username})) (catch Exception e))
                                       ))})))
)
