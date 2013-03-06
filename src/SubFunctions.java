import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SubFunctions implements ISubFunctions {
    public void writeFile(File file, File currentDirectory, String text) {
        BufferedWriter bw = null;
        try {
            //TODO на .txt file second extension
            //file=new File(file+".txt");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            StringReader stringReader = new StringReader(text);
            BufferedReader br = new BufferedReader(stringReader);

            for(String line = br.readLine(); line != null; line = br.readLine()) {
                bw.write(line);
                bw.newLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bw) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

	@Override
	public List<String> readFile(File filename) {
		// StringBuffer sb = new StringBuffer(2048);
		List<String> lines = new ArrayList<String>();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String str;
			while ((str = in.readLine()) != null) {
				// sb.append(str + "\n");
				lines.add(str);
			}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
            System.out.println("Файл не найден ");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// return sb.toString();
		return lines;
	}

    @Override
    public String getText(File filename) {
        StringBuffer sb = new StringBuffer(2048);

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(
                    filename)));
            String str;
            while ((str = in.readLine()) != null) {
                sb.append(str + "\n");

            }
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Файл не найден ");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();

    }

	@Override
	public List<String> readFile(String filename) {
		// StringBuffer sb = new StringBuffer(2048);
		List<String> lines = new ArrayList<String>();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename)));
			String str;
			while ((str = in.readLine()) != null) {
				// sb.append(str + "\n");
				lines.add(str);
			}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
            System.out.println("Файл не найден ");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// return sb.toString();
		return lines;
	}

	public void writeFile(File filename, String text) {
		BufferedWriter bw = null;
		try {
			// TODO �� .txt file second extension
			// file=new File(file+".txt");

			if (!filename.exists()) {
				filename.createNewFile();
			}

			FileWriter fw = new FileWriter(filename.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			StringReader stringReader = new StringReader(text);
			BufferedReader br = new BufferedReader(stringReader);

			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				bw.write(line);
				bw.newLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}


}
