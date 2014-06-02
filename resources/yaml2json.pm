package yaml2json;
use Moose;
use YAML;
use JSON;
use YAML::Syck;

$YAML::Syck::ImplicitTyping = 1;

sub run {
    my $filepath = "/Users/mburns/Sandbox/clojure/clj-salt/resources/system_state.yaml";
    my $data = LoadFile($filepath);
    # my $data = YAML::LoadFile($filepath);
    my $json = encode_json $data;
    print $json;
}

run() unless caller;
