package de.noah_ruben.data

import de.noah_ruben.data.model.Project
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.time.LocalDateTime

typealias ProjectsCache = Pair<LocalDateTime, List<Project>>

object Cache {
    lateinit var githubClient: RepositoryClient

    private val logger = LoggerFactory.getLogger(Cache::class.java)
    private var projectsCache: ProjectsCache

    init {
        projectsCache = (LocalDateTime.now() to emptyList())
    }

    fun getAllTopics(): Set<String> {
        val topics = mutableSetOf<String>()
        getProjects().forEach {
            topics.addAll(it.topics)
        }
        return topics
    }

    fun getAllLanguages(): Set<String> {
        val languages = mutableSetOf<String>()
        getProjects().forEach {
            languages.addAll(it.languages)
        }
        return languages
    }

    // TODO: responde directly and repopulate cache afterwards?
    fun getProjects(): List<Project> {
        val (lastCached: LocalDateTime, projects: List<Project>) = projectsCache

        if (projects.isEmpty() || lastCached.isBefore(LocalDateTime.now().minusMinutes(15))) {
            logger.info("Cache miss for projects! Last fetch: $lastCached")

            // TODO: Error handling
            val fetchedProjects = runBlocking {
                val repositories = githubClient.getRepositories()
                return@runBlocking repositories.map {
                    val languages = githubClient.getRepositoryLanguages(it.name) {
                        sleep(200)
                    }
                    if (it.created_at.isNullOrBlank() || it.pushed_at.isNullOrBlank()) throw Error("Invalid repository")
                    Project(
                        stars = it.stargazers_count,
                        topics = it.topics,
                        languages = languages,
                        releases = it.created_at.toString(),
                        name = it.name,
                        description = it.description.orEmpty(),
                        githubLink = it.html_url.orEmpty(),
                        link = it.language.orEmpty(),
                        lastModified = LocalDateTime.parse(it.pushed_at),
                        created = LocalDateTime.parse(it.created_at),
                    )
                }
            }

            projectsCache = (LocalDateTime.now() to fetchedProjects)
            logger.debug("Put projects into cache {}", projectsCache.first)
            return projects
        } else {
            logger.debug("Cache hit for projects. Using cache from {}!", lastCached)
            return projects
        }
    }

    fun initialize() {
        getProjects()
    }
}
