require 'byebug'
file = File.new('urls.txt','r');
outfile = File.new('img_url.txt','r');
out_hash = Hash.new
while line = outfile.gets
  #puts line.split(",")[0]
  out_hash[line.split(",")[0]] = true
end

while (line = file.gets)
  line = line.strip()
  unless out_hash[line]
    puts line
  end
end