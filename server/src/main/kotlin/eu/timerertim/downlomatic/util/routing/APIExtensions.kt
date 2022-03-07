package eu.timerertim.downlomatic.util.routing

import eu.timerertim.downlomatic.api.APIPath
import eu.timerertim.downlomatic.api.APIPathArgument
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

@ContextDsl
fun Route.get(
    path: APIPath,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
) = get(path.path, body)

@ContextDsl
fun Route.post(
    path: APIPath,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
) = post(path.path, body)

@ContextDsl
fun Route.put(
    path: APIPath,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
) = put(path.path, body)

@ContextDsl
fun Route.head(
    path: APIPath,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
) = head(path.path, body)

@ContextDsl
fun Route.delete(
    path: APIPath,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
) = delete(path.path, body)

@ContextDsl
fun Route.patch(
    path: APIPath,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
) = patch(path.path, body)

@ContextDsl
fun Route.options(
    path: APIPath,
    body: suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
) = options(path.path, body)

operator fun Parameters.get(parameter: APIPathArgument) = get(parameter.name)

fun Parameters.getOrDefault(parameter: APIPathArgument) = get(parameter.name) ?: parameter.getDefault()

fun Parameters.getAll(parameter: APIPathArgument) = getAll(parameter.name)
