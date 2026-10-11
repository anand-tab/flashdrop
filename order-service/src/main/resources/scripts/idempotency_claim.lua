local existing = redis.call('GET' , KEYS[1])


if not existing then
  redis.call('SET', KEYS[1], 'IN_PROGRESS|' .. ARGV[1], 'EX', ARGV[2])
  return {'NEW'}
end

-- stored formats: IN_PROGRESS|<fp>   or   DONE|<orderId>|<fp>
local parts = {}
for p in string.gmatch(existing, '([^|]+)') do
  table.insert(parts, p)
end

local state = parts[1]
local fp = (state == 'DONE') and parts[3] or parts[2]

if fp ~= ARGV[1] then
  return {'MISMATCH'}
end

if state == 'DONE' then
  return {'DONE', parts[2]}
end

return {'IN_PROGRESS'}