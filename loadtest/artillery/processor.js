function setOrderPayload(userContext, events, done) {
  const sku = Math.floor(Math.random() * 10)
  const orderId = `order-${Date.now()}-${Math.floor(Math.random() * 1000)}`
  userContext.vars.orderPayload = {
    clientOrderId: orderId,
    items: [
      { productCode: `SKU-${sku}`, quantity: 1 + Math.floor(Math.random() * 3) },
      { productCode: `SKU-${(sku + 3) % 10}`, quantity: 1 }
    ]
  }
  return done()
}

function setTransferPayload(userContext, events, done) {
  const from = `A-${1 + Math.floor(Math.random() * 5)}`
  let to = `A-${1 + Math.floor(Math.random() * 5)}`
  if (to === from) {
    const index = parseInt(from.split('-')[1], 10)
    to = `A-${(index % 5) + 1}`
  }
  userContext.vars.transferPayload = {
    fromAccountId: from,
    toAccountId: to,
    amount: 1000 + Math.floor(Math.random() * 4000)
  }
  return done()
}

module.exports = { setOrderPayload, setTransferPayload }
