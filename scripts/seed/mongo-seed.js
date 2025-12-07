const dbName = 'mongo-tx-test'
const conn = connect('mongodb://localhost:27017/' + dbName)

print('Seeding database: ' + dbName)

conn.accounts.drop()
conn.inventories.drop()
conn.point_ledgers.drop()

conn.accounts.insertMany([
  { _id: 'A-1', ownerName: '테스트계정1', pointBalance: 10000, updatedAt: ISODate() },
  { _id: 'A-2', ownerName: '테스트계정2', pointBalance: 8000, updatedAt: ISODate() },
  { _id: 'A-3', ownerName: '테스트계정3', pointBalance: 5000, updatedAt: ISODate() },
  { _id: 'A-4', ownerName: '테스트계정4', pointBalance: 12000, updatedAt: ISODate() },
  { _id: 'A-5', ownerName: '테스트계정5', pointBalance: 15000, updatedAt: ISODate() },
])

const inventoryDocs = []
for (let i = 0; i < 10; i++) {
  inventoryDocs.push({ _id: `SKU-${i}`, quantity: 500 })
}
conn.inventories.insertMany(inventoryDocs)

print('Accounts: ' + conn.accounts.countDocuments({}))
print('Inventories: ' + conn.inventories.countDocuments({}))
print('Seed completed')
