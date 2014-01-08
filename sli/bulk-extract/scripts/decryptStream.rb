require 'openssl'
puts("Usage: rubyDecrypt <private key> <input file> <output file>")
if ARGV.size != 3
  exit
end
private_key = OpenSSL::PKey::RSA.new File.read ARGV[0]
input_file = File.open(ARGV[1], 'rb')
puts "length #{input_file.size}"
encryptediv = input_file.read(256)
encryptedsecret = input_file.read(256)
 
decrypted_iv = private_key.private_decrypt(encryptediv)
decrypted_secret = private_key.private_decrypt(encryptedsecret)
 
aes = OpenSSL::Cipher.new('AES-128-CBC')
aes.decrypt
aes.key = decrypted_secret
aes.iv = decrypted_iv
 
output_file = File.open(ARGV[2], 'wb')
 
loop do
        r = input_file.read(4096)
        break unless r
        plain = aes.update(r)
        output_file << plain
end
 
output_file << aes.final