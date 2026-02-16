def call() {
    return {
        def stages = load('./jenkins/stages.groovy')

        stages.buildAndTest()()
    }
}

return this

