package me.ramos.mongotxtest.adapter.`in`.accountWebIn

import me.ramos.mongotxtest.application.dto.command.TransferPointCommand
import me.ramos.mongotxtest.application.port.inbound.TransferPointInPort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/accounts")
class AccountV1Controller(
    private val transferPointInPort: TransferPointInPort,
) {

    @PostMapping("/transfer")
    fun transfer(@RequestBody request: TransferPointRequest): ResponseEntity<TransferPointResponse> {
        val command = TransferPointCommand(
            fromAccountId = request.fromAccountId,
            toAccountId = request.toAccountId,
            amount = request.amount,
        )
        val result = transferPointInPort.transfer(command)
        val response = TransferPointResponse(result.fromAccountId, result.toAccountId, result.amount)
        return ResponseEntity.ok(response)
    }
}

data class TransferPointRequest(
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Long,
)

data class TransferPointResponse(
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Long,
)
