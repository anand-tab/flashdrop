local expiry = tonumber(ARGV[1])

for i = 2, #ARGV, 2 do
    local key = ARGV[i]
    local value = ARGV[i+1]

    redis.call('SET', key, value)
    redis.call('EXPIRE', key, expiry)
end

return "SUCCESS"