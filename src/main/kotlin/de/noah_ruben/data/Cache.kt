package de.noah_ruben.data

import de.noah_ruben.config.HEALTH_DOWN
import de.noah_ruben.config.HEALTH_OK
import de.noah_ruben.config.HealthCheckResult
import de.noah_ruben.data.model.Project
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                    val languages = githubClient.getRepositoryLanguages(it.name)
                    if (it.createdAt.isNullOrBlank() || it.pushedAt.isNullOrBlank()) throw Error("Invalid repository")
                    Project(
                        stars = it.stargazersCount,
                        topics = it.topics,
                        languages = languages,
                        releases = it.createdAt.toString(),
                        name = it.name,
                        description = it.description.orEmpty(),
                        githubLink = it.htmlUrl.orEmpty(),
                        link = it.homepage.orEmpty(),

                        lastModified = LocalDateTime.parse(it.pushedAt, DateTimeFormatter.ISO_DATE_TIME),
                        created = LocalDateTime.parse(it.createdAt, DateTimeFormatter.ISO_DATE_TIME),
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

    fun healthCheck(): HealthCheckResult {
        if (!::githubClient.isInitialized) {
            return HealthCheckResult(
                status = HEALTH_DOWN,
                message = "Repository client is not initialized.",
            )
        }

        val (lastCached, projects) = projectsCache
        return HealthCheckResult(
            status = HEALTH_OK,
            message = "Projects cache is available with ${projects.size} cached project(s) from $lastCached.",
        )
    }
}
