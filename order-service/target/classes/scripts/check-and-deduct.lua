local key = KEYS[1]
local requested = tonumber(ARGV[1])
local current = tonumber(redis.call('GET', key))
if current == nil then
    return -1
end
if current < requested then
    return -2
end
redis.call('DECRBY', key, requested)
return current - requested
