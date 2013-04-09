require 'openssl'

puts("Usage: rubyDecrypt <private key> <input file> <output file>")
if ARGV.size != 3
  exit 
end
private_key = OpenSSL::PKey::RSA.new File.read ARGV[0]
input_file = File.open(ARGV[1], 'r')
puts "length #{input_file.size}"
encryptediv = input_file.read(256) 
#encryptediv = input_file[0,256] 
encryptedsecret = input_file.read(256)
#encryptedsecret = input_file[256,256]
encryptedmessage = input_file.read()
#encryptedmessage = input_file[512,input_file.length - 512]

decrypted_iv = private_key.private_decrypt(encryptediv)
decrypted_secret = private_key.private_decrypt(encryptedsecret)

aes = OpenSSL::Cipher.new('AES-128-CBC')
aes.decrypt
aes.key = decrypted_secret
aes.iv = decrypted_iv
plain = aes.update(encryptedmessage) + aes.final

puts("Final is #{aes.final}")
puts("IV is #{encryptediv}")
puts("Decrypted iv type is #{decrypted_iv.class} and it is #{decrypted_iv}")
puts("Encrypted message is #{encryptedmessage}")
puts("Cipher is #{aes}")

output_file = File.open(ARGV[2], 'w')
output_file.write(plain)
