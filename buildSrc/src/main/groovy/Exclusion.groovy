class Exclusion {
    Node dependencies

    Exclusion(Node dependencies) {
        this.dependencies = dependencies
    }

    static void create(Node dependencies, Closure closure) {
        closure.delegate = new Exclusion(dependencies)
        closure()
    }

    void fromDependency(Map args, Closure closure) {
        closure.delegate = new ExcludedDependency(args)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
    }

    class ExcludedDependency {
        String groupId
        String artifactId
        String version

        ExcludedDependency(Map args) {
            this.groupId = args.groupId
            this.artifactId = args.artifactId
            this.version = args.version
        }

        void exclude(Map exclusionArgs) {
            Node dependency = dependencies.depthFirst().find({ Node it ->
                it.name() == 'dependency'
                        && it.groupId.text() == groupId
                        && it.artifactId.text() == artifactId
                        && it.version.text() == version
            })

            if (null != dependency) {
                Node exclusionNode = (dependency.exclusions[0] as Node
                        ?: dependency.appendNode('exclusions').appendNode('exclusion'))
                exclusionNode.appendNode('groupId', exclusionArgs.groupId)
                exclusionNode.appendNode('artifactId', exclusionArgs.artifactId)
            }
        }

    }
}